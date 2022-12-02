package me.marylieh.simplewarp.utils

import me.marylieh.simplewarp.utils.Updater.UpdateType
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Level
import java.util.zip.ZipFile

/**
 * @author Gravity
 * @version 2.4
 */
class Updater(/* User-provided variables */ // Plugin running Updater
              private val plugin: Plugin, // Project's Curse ID
              private val id: Int, // The plugin file (jar)
              private val file: File, // Type of update check to run
              private val type: UpdateType, callback: UpdateCallback?, // Whether to announce file downloads
              private val announce: Boolean
) {
    // The folder that downloads will be placed in
    private val updateFolder: File

    // The provided callback (if any)
    private val callback: UpdateCallback?

    // BukkitDev ServerMods API key
    private var apiKey: String? = null

    /* Collected from Curse API */
    private var versionName: String? = null
    private var versionLink: String? = null

    /* Update process variables */ // Connection to RSS
    private var url: URL? = null

    // Used for determining the outcome of the update process
    private var result = UpdateResult.SUCCESS

    enum class UpdateResult {
        /**
         * The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.
         */
        SUCCESS,

        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE,

        /**
         * The server administrator has disabled the updating system.
         */
        DISABLED,

        /**
         * The updater found an update, but was unable to download it.
         */
        FAIL_DOWNLOAD,

        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO,

        /**
         * When running the version check, the file on DBO did not contain a recognizable version.
         */
        FAIL_NOVERSION,

        /**
         * The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADID,

        /**
         * The server administrator has improperly configured their API key in the configuration.
         */
        FAIL_APIKEY,

        /**
         * The updater found an update, but because of the UpdateType being set to NO_DOWNLOAD, it wasn't downloaded.
         */
        UPDATE_AVAILABLE
    }

    /**
     * Allows the developer to specify the type of update that will be run.
     */
    enum class UpdateType {
        /**
         * Run a version check, and then if the file is out of date, download the newest version.
         */
        DEFAULT,

        /**
         * Don't run a version check, just find the latest update and download it.
         */
        NO_VERSION_CHECK,

        /**
         * Get information about the version and the download size, but don't actually download anything.
         */
        NO_DOWNLOAD
    }

    /**
     * Initialize the updater.
     *
     * @param plugin   The plugin that is checking for an update.
     * @param id       The dev.bukkit.org id of the project.
     * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     * @param type     Specify the type of update this will be. See [UpdateType]
     * @param announce True if the program should announce the progress of new updates in console.
     */
    constructor(plugin: Plugin, id: Int, file: File, type: UpdateType, announce: Boolean) : this(
        plugin,
        id,
        file,
        type,
        null,
        announce
    ) {
    }

    /**
     * Initialize the updater with the provided callback.
     *
     * @param plugin   The plugin that is checking for an update.
     * @param id       The dev.bukkit.org id of the project.
     * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     * @param type     Specify the type of update this will be. See [UpdateType]
     * @param callback The callback instance to notify when the Updater has finished
     * @param announce True if the program should announce the progress of new updates in console.
     */
    init {
        updateFolder = plugin.server.updateFolderFile
        this.callback = callback
        val pluginFile = plugin.dataFolder.parentFile
        val updaterFile = File(pluginFile, "Updater")
        val updaterConfigFile = File(updaterFile, "config.yml")
        val config = YamlConfiguration()
        config.options()
            .header(/* !!! Hit visitElement for element type: class org.jetbrains.kotlin.nj2k.tree.JKErrorExpression !!! */)
        config.addDefault(API_KEY_CONFIG_KEY, API_KEY_DEFAULT)
        config.addDefault(DISABLE_CONFIG_KEY, DISABLE_DEFAULT)
        if (!updaterFile.exists()) {
            fileIOOrError(updaterFile, updaterFile.mkdir(), true)
        }
        val createFile = !updaterConfigFile.exists()
        try {
            if (createFile) {
                fileIOOrError(updaterConfigFile, updaterConfigFile.createNewFile(), true)
                config.options().copyDefaults(true)
                config.save(updaterConfigFile)
            } else {
                config.load(updaterConfigFile)
            }
        } catch (e: Exception) {
            val message: String
            message = if (createFile) {
                "The updater could not create configuration at " + updaterFile.absolutePath
            } else {
                "The updater could not load configuration at " + updaterFile.absolutePath
            }
            plugin.logger.log(Level.SEVERE, message, e)
        }
        if (config.getBoolean(DISABLE_CONFIG_KEY)) {
            result = UpdateResult.DISABLED
        }
        var key = config.getString(API_KEY_CONFIG_KEY)
        if (API_KEY_DEFAULT.equals(key, ignoreCase = true) || "" == key) {
            key = null
        }
        apiKey = key
        try {
            url = URL(HOST + QUERY + id)
        } catch (e: MalformedURLException) {
            plugin.logger.log(Level.SEVERE, "The project ID provided for updating, " + id + " is invalid.", e)
            result = UpdateResult.FAIL_BADID
        }
        if (result != UpdateResult.FAIL_BADID) {
            // Updater thread
            val thread = Thread(UpdateRunnable())
            thread.start()
        } else {
            runUpdater()
        }
    }

    private fun saveFile(file: String) {
        val folder = updateFolder
        deleteOldFiles()
        if (!folder.exists()) {
            fileIOOrError(folder, folder.mkdir(), true)
        }
        downloadFile()

        // Check to see if it's a zip file, if it is, unzip it.
        val dFile = File(folder.absolutePath, file)
        if (dFile.name.endsWith(".zip")) {
            // Unzip
            this.unzip(dFile.absolutePath)
        }
        if (announce) {
            plugin.logger.info("Finished updating.")
        }
    }

    /**
     * Download a file and save it to the specified folder.
     */
    private fun downloadFile() {
        var `in`: BufferedInputStream? = null
        var fout: FileOutputStream? = null
        try {
            val fileUrl = followRedirects(versionLink)
            val fileLength = fileUrl.openConnection().contentLength
            `in` = BufferedInputStream(fileUrl.openStream())
            fout = FileOutputStream(File(updateFolder, file.name))
            val data = ByteArray(BYTE_SIZE)
            var count: Int
            if (announce) {
                plugin.logger.info("About to download a new update: " + versionName)
            }
            var downloaded: Long = 0
            while (`in`.read(data, 0, BYTE_SIZE).also { count = it } != -1) {
                downloaded += count.toLong()
                fout.write(data, 0, count)
                val percent = (downloaded * 100 / fileLength).toInt()
                if (announce && percent % 10 == 0) {
                    plugin.logger.info("Downloading update: $percent% of $fileLength bytes.")
                }
            }
        } catch (ex: Exception) {
            plugin.logger.log(
                Level.WARNING,
                "The auto-updater tried to download a new update, but was unsuccessful.",
                ex
            )
            result = UpdateResult.FAIL_DOWNLOAD
        } finally {
            try {
                `in`?.close()
            } catch (ex: IOException) {
                plugin.logger.log(Level.SEVERE, null, ex)
            }
            try {
                fout?.close()
            } catch (ex: IOException) {
                plugin.logger.log(Level.SEVERE, null, ex)
            }
        }
    }

    @Throws(IOException::class)
    private fun followRedirects(location: String?): URL {
        var location = location
        var resourceUrl: URL
        var base: URL
        var next: URL
        var conn: HttpURLConnection
        var redLoc: String?
        while (true) {
            resourceUrl = URL(location)
            conn = resourceUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", "Mozilla/5.0...")
            when (conn.responseCode) {
                HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP -> {
                    redLoc = conn.getHeaderField("Location")
                    base = URL(location)
                    next = URL(base, redLoc) // Deal with relative URLs
                    location = next.toExternalForm()
                    continue
                }
            }
            break
        }
        return conn.url
    }

    /**
     * Remove possibly leftover files from the update folder.
     */
    private fun deleteOldFiles() {
        //Just a quick check to make sure we didn't leave any files from last time...
        val list = listFilesOrError(updateFolder)
        for (xFile in list) {
            if (xFile!!.name.endsWith(".zip")) {
                fileIOOrError(xFile, xFile.mkdir(), true)
            }
        }
    }

    /**
     * Part of Zip-File-Extractor, modified by Gravity for use with Updater.
     *
     * @param file the location of the file to extract.
     */
    private fun unzip(file: String) {
        val fSourceZip = File(file)
        try {
            val zipPath = file.substring(0, file.length - 4)
            val zipFile = ZipFile(fSourceZip)
            val e = zipFile.entries()
            while (e.hasMoreElements()) {
                val entry = e.nextElement()
                val destinationFilePath = File(zipPath, entry.name)
                fileIOOrError(destinationFilePath.parentFile, destinationFilePath.parentFile.mkdirs(), true)
                if (!entry.isDirectory) {
                    val bis = BufferedInputStream(zipFile.getInputStream(entry))
                    var b: Int
                    val buffer = ByteArray(BYTE_SIZE)
                    val fos = FileOutputStream(destinationFilePath)
                    val bos = BufferedOutputStream(fos, BYTE_SIZE)
                    while (bis.read(buffer, 0, BYTE_SIZE).also { b = it } != -1) {
                        bos.write(buffer, 0, b)
                    }
                    bos.flush()
                    bos.close()
                    bis.close()
                    val name = destinationFilePath.name
                    if (name.endsWith(".jar") && pluginExists(name)) {
                        val output = File(updateFolder, name)
                        fileIOOrError(output, destinationFilePath.renameTo(output), true)
                    }
                }
            }
            zipFile.close()

            // Move any plugin data folders that were included to the right place, Bukkit won't do this for us.
            moveNewZipFiles(zipPath)
        } catch (e: IOException) {
            plugin.logger.log(
                Level.SEVERE,
                "The auto-updater tried to unzip a new update file, but was unsuccessful.",
                e
            )
            result = UpdateResult.FAIL_DOWNLOAD
        } finally {
            fileIOOrError(fSourceZip, fSourceZip.delete(), false)
        }
    }

    /**
     * Find any new files extracted from an update into the plugin's data directory.
     * @param zipPath path of extracted files.
     */
    private fun moveNewZipFiles(zipPath: String) {
        val list = listFilesOrError(File(zipPath))
        for (dFile in list) {
            if (dFile!!.isDirectory && pluginExists(dFile.name)) {
                // Current dir
                val oFile = File(plugin.dataFolder.parent, dFile.name)
                // List of existing files in the new dir
                val dList = listFilesOrError(dFile)
                // List of existing files in the current dir
                val oList = listFilesOrError(oFile)
                for (cFile in dList) {
                    // Loop through all the files in the new dir
                    var found = false
                    for (xFile in oList) {
                        // Loop through all the contents in the current dir to see if it exists
                        if (xFile!!.name == cFile!!.name) {
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        // Move the new file into the current dir
                        val output = File(oFile, cFile!!.name)
                        fileIOOrError(output, cFile.renameTo(output), true)
                    } else {
                        // This file already exists, so we don't need it anymore.
                        fileIOOrError(cFile, cFile!!.delete(), false)
                    }
                }
            }
            fileIOOrError(dFile, dFile.delete(), false)
        }
        val zip = File(zipPath)
        fileIOOrError(zip, zip.delete(), false)
    }

    /**
     * Check if the name of a jar is one of the plugins currently installed, used for extracting the correct files out of a zip.
     *
     * @param name a name to check for inside the plugins folder.
     * @return true if a file inside the plugins folder is named this.
     */
    private fun pluginExists(name: String): Boolean {
        val plugins = listFilesOrError(File("plugins"))
        for (file in plugins) {
            if (file!!.name == name) {
                return true
            }
        }
        return false
    }

    /**
     * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
     *
     * @return true if the version was located and is not the same as the remote's newest.
     */
    private fun versionCheck(): Boolean {
        val title = versionName
        if (type != UpdateType.NO_VERSION_CHECK) {
            val localVersion = plugin.description.version
            if (title!!.split(DELIMETER.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray().size >= 2) {
                // Get the newest file's version number
                val remoteVersion = title.split(DELIMETER.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[title.split(DELIMETER.toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray().size - 1].split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0]
                if (hasTag(localVersion) || !shouldUpdate(localVersion, remoteVersion)) {
                    // We already have the latest version, or this build is tagged for no-update
                    result = UpdateResult.NO_UPDATE
                    return false
                }
            } else {
                // The file's name did not contain the string 'vVersion'
                val authorInfo =
                    if (plugin.description.authors.isEmpty()) "" else " (" + plugin.description.authors[0] + ")"
                plugin.logger.warning("The author of this plugin$authorInfo has misconfigured their Auto Update system")
                plugin.logger.warning("File versions should follow the format 'PluginName vVERSION'")
                plugin.logger.warning("Please notify the author of this error.")
                result = UpdateResult.FAIL_NOVERSION
                return false
            }
        }
        return true
    }

    /**
     * **If you wish to run mathematical versioning checks, edit this method.**
     *
     *
     * With default behavior, Updater will NOT verify that a remote version available on BukkitDev
     * which is not this version is indeed an "update".
     * If a version is present on BukkitDev that is not the version that is currently running,
     * Updater will assume that it is a newer version.
     * This is because there is no standard versioning scheme, and creating a calculation that can
     * determine whether a new update is actually an update is sometimes extremely complicated.
     *
     *
     *
     * Updater will call this method from [.versionCheck] before deciding whether
     * the remote version is actually an update.
     * If you have a specific versioning scheme with which a mathematical determination can
     * be reliably made to decide whether one version is higher than another, you may
     * revise this method, using the local and remote version parameters, to execute the
     * appropriate check.
     *
     *
     *
     * Returning a value of **false** will tell the update process that this is NOT a new version.
     * Without revision, this method will always consider a remote version at all different from
     * that of the local version a new update.
     *
     * @param localVersion the current version
     * @param remoteVersion the remote version
     * @return true if Updater should consider the remote version an update, false if not.
     */
    fun shouldUpdate(localVersion: String, remoteVersion: String?): Boolean {
        return !localVersion.equals(remoteVersion, ignoreCase = true)
    }

    /**
     * Evaluate whether the version number is marked showing that it should not be updated by this program.
     *
     * @param version a version number to check for tags in.
     * @return true if updating should be disabled.
     */
    private fun hasTag(version: String): Boolean {
        for (string in NO_UPDATE_TAG) {
            if (version.contains(string)) {
                return true
            }
        }
        return false
    }

    /**
     * Make a connection to the BukkitDev API and request the newest file's details.
     *
     * @return true if successful.
     */
    private fun read(): Boolean {
        return try {
            val conn = url!!.openConnection()
            conn.connectTimeout = 5000
            if (apiKey != null) {
                conn.addRequestProperty("X-API-Key", apiKey)
            }
            conn.addRequestProperty("User-Agent", USER_AGENT)
            conn.doOutput = true
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            val response = reader.readLine()
            val array = JSONValue.parse(response) as JSONArray
            if (array.isEmpty()) {
                plugin.logger.warning("The updater could not find any files for the project id " + id)
                result = UpdateResult.FAIL_BADID
                return false
            }
            val latestUpdate = array[array.size - 1] as JSONObject
            versionName = latestUpdate[TITLE_VALUE] as String?
            versionLink = latestUpdate[LINK_VALUE] as String?
            latestUpdate[TYPE_VALUE]
            latestUpdate[VERSION_VALUE]
            true
        } catch (e: IOException) {
            if (e.message!!.contains("HTTP response code: 403")) {
                plugin.logger.severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml")
                plugin.logger.severe("Please double-check your configuration to ensure it is correct.")
                result = UpdateResult.FAIL_APIKEY
            } else {
                plugin.logger.severe("The updater could not contact dev.bukkit.org for updating.")
                plugin.logger.severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.")
                result = UpdateResult.FAIL_DBO
            }
            plugin.logger.log(Level.SEVERE, null, e)
            false
        }
    }

    /**
     * Perform a file operation and log any errors if it fails.
     * @param file file operation is performed on.
     * @param result result of file operation.
     * @param create true if a file is being created, false if deleted.
     */
    private fun fileIOOrError(file: File?, result: Boolean, create: Boolean) {
        if (!result) {
            plugin.logger.severe("The updater could not " + (if (create) "create" else "delete") + " file at: " + file!!.absolutePath)
        }
    }

    private fun listFilesOrError(folder: File?): Array<File?> {
        val contents = folder!!.listFiles()
        return if (contents == null) {
            plugin.logger.severe("The updater could not access files at: " + updateFolder.absolutePath)
            arrayOfNulls(0)
        } else {
            contents
        }
    }

    /**
     * Called on main thread when the Updater has finished working, regardless
     * of result.
     */
    interface UpdateCallback {
        /**
         * Called when the updater has finished working.
         * @param updater The updater instance
         */
        fun onFinish(updater: Updater?)
    }

    private inner class UpdateRunnable : Runnable {
        override fun run() {
            runUpdater()
        }
    }

    private fun runUpdater() {
        if (url != null && read() && versionCheck()) {
            // Obtain the results of the project's file feed
            if (versionLink != null && type != UpdateType.NO_DOWNLOAD) {
                var name = file.name
                // If it's a zip file, it shouldn't be downloaded as the plugin's name
                if (versionLink!!.endsWith(".zip")) {
                    name = versionLink!!.substring(versionLink!!.lastIndexOf("/") + 1)
                }
                saveFile(name)
            } else {
                result = UpdateResult.UPDATE_AVAILABLE
            }
        }
        if (callback != null) {
            object : BukkitRunnable() {
                override fun run() {
                    runCallback()
                }
            }.runTask(plugin)
        }
    }

    private fun runCallback() {
        callback!!.onFinish(this)
    }

    companion object {
        /* Constants */ // Remote file's title
        private const val TITLE_VALUE = "name"

        // Remote file's download link
        private const val LINK_VALUE = "downloadUrl"

        // Remote file's release type
        private const val TYPE_VALUE = "releaseType"

        // Remote file's build version
        private const val VERSION_VALUE = "gameVersion"

        // Path to GET
        private const val QUERY = "/servermods/files?projectIds="

        // Slugs will be appended to this to get to the project's RSS feed
        private const val HOST = "https://api.curseforge.com"

        // User-agent when querying Curse
        private const val USER_AGENT = "Updater (by Gravity)"

        // Used for locating version numbers in file names
        private const val DELIMETER = "^v|[\\s_-]v"

        // If the version number contains one of these, don't update.
        private val NO_UPDATE_TAG = arrayOf("-DEV", "-PRE", "-SNAPSHOT")

        // Used for downloading files
        private const val BYTE_SIZE = 1024

        // Config key for api key
        private const val API_KEY_CONFIG_KEY = "api-key"

        // Config key for disabling Updater
        private const val DISABLE_CONFIG_KEY = "disable"

        // Default api key value in config
        private const val API_KEY_DEFAULT = "PUT_API_KEY_HERE"

        // Default disable value in config
        private const val DISABLE_DEFAULT = false
    }
}