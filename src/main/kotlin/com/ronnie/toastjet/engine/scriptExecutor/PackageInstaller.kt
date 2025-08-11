package com.ronnie.toastjet.engine.scriptExecutor

import java.io.File
import java.util.logging.Logger

object PackageInstaller {
    private var packageManager: String = "npm"

    private val LOG = Logger.getLogger(this::class.java.name)

    init {
        detectPackageManager()
    }

    private fun detectPackageManager(): String? {
        val managers = listOf("pnpm", "yarn", "npm", "bun")

        for (manager in managers) {
            if (isCommandAvailable(manager)) {
                packageManager = manager
                println("Using $manager as the package manager.")
                return manager
            }
        }

        return null
    }

    private fun isCommandAvailable(command: String): Boolean {
        return try {
            val process = ProcessBuilder(command, "--version")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    fun install(packageName: String, version: String = "latest") {
        val resourceDir = File("src/main/resources/Scripts")
        val nodeModules = File(resourceDir, "node_modules")

        val manager = detectPackageManager()

        if (!nodeModules.exists()) {
            println("node_modules not found. Initializing...")
            resourceDir.mkdirs()

            val initCommand = when (manager) {
                "yarn" -> "yarn install"
                "pnpm" -> "pnpm install"
                "npm" -> "npm install"
                "bun" -> "bun install"
                else -> error("Unsupported package manager: $manager")
            }

            runCommand(resourceDir, initCommand)
        }

        println("Installing package: $packageName@$version")

        val installCommand = when (manager) {
            "yarn" -> "yarn add $packageName@$version"
            "pnpm" -> "pnpm add $packageName@$version"
            "npm" -> "npm install $packageName@$version"
            "bun" -> "bun add $packageName@$version"
            else -> error("Unsupported package manager: $manager")
        }

        runCommand(resourceDir, installCommand)
    }

    fun runCommand(workingDir: File, command: String) {
        try {
            val process = ProcessBuilder(command)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                LOG.warning("Command failed with exit code $exitCode: $command")
                error("Command failed with exit code $exitCode")
            } else {
                LOG.info("Command succeeded: $command")
            }
        } catch (e: Exception) {
            LOG.warning("Failed to execute command: $command $e")
            throw RuntimeException("Failed to run command: $command", e)
        }
    }

    // === NEW FUNCTION ===
    fun setupToastApiWorkspace(): File {
        val toastApiDir = File(System.getProperty("user.home"), ".toastApi")
        val nodeModules = File(toastApiDir, "node_modules")

        if (nodeModules.exists() && nodeModules.isDirectory) {
            LOG.info("toastApi: node_modules already exists. Skipping dependency installation.")
            return toastApiDir
        }

        if (!toastApiDir.exists()) {
            toastApiDir.mkdirs()
            LOG.info("Created toastApi workspace at: ${toastApiDir.absolutePath}")
        }

        val packageJsonFile = File(toastApiDir, "package.json")
        if (!packageJsonFile.exists()) {
            val packageJsonContent = """
                {
                  "dependencies": {
                    "@faker-js/faker": "^9.7.0",
                    "lodash": "^4.17.21",
                    "validator": "^13.15.0"
                  },
                  "type": "commonjs"
                }
            """.trimIndent()

            packageJsonFile.writeText(packageJsonContent)
            LOG.info("Created package.json in toastApi workspace.")
        }

        val installCommand = when (packageManager.lowercase()) {
            "yarn" -> listOf("yarn", "install")
            "pnpm" -> listOf("pnpm", "install")
            "npm" -> listOf("npm", "install")
            "bun" -> listOf("bun", "install")
            else -> error("Unsupported package manager: $packageManager")
        }

        LOG.info("Running command: ${installCommand.joinToString(" ")} in ${toastApiDir.absolutePath}")

        runCommand(toastApiDir, installCommand.joinToString(" "))

        LOG.info("toastApi workspace setup completed.")
        return toastApiDir
    }
}