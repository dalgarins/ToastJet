package com.ronnie.toastjet.engine

import java.io.File

object PackageInstaller {
    private var packageManager: String = "npm"

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
        } catch (e: Exception) {
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
            val parts = command.split(" ")
            val process = ProcessBuilder(parts)
                .directory(workingDir)
                .inheritIO()
                .start()

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                error("Command failed with exit code $exitCode: $command")
            }
        } catch (e: Exception) {
            error("Failed to execute command: $command\n${e.message}")
        }
    }

}
