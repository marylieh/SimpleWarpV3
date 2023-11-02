import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.tools.ant.filters.FixCrLfFilter
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.gmazzo.buildconfig") version "3.0.0"
}

group = "me.marylieh.simplewarp"
version = "1.0.0"

repositories {
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

val minecraft_version: String by project

dependencies {
    // PaperMC Dependency
    compileOnly("com.destroystokyo.paper", "paper-api", "$minecraft_version-R0.1-SNAPSHOT")

    // Add your dependencies here
    compileOnly("org.bstats", "bstats-bukkit", "3.0.2")
}

buildConfig {
    className("BuildConfig")
    packageName("$group.$name")
    val commit = getGitHash()
    val branch = getGitBranch()
    buildConfigField("String", "GIT_COMMIT", "\"$commit\"")
    buildConfigField("String", "GIT_BRANCH", "\"$branch\"")
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine( "git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine( "git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

tasks {
    processResources {
        filter(FixCrLfFilter::class)
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to project.version))
        filteringCharset = "UTF-8"
    }
    jar {
        // Disabled, because we use the shadowJar task for building our jar
        enabled = false
    }
    shadowJar {
        relocate("org.bstats", "me.marylieh.simplewarp")
    }
    build {
        dependsOn(shadowJar)
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
