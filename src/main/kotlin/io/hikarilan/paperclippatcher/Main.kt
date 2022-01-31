package io.hikarilan.paperclippatcher

import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val files = scanFile()
        if (files.isEmpty()) {
            println("No file need to be patched")
            return
        }
        println("Attempting to patch ${files.size} file(s)\n")
        files.forEach { findAndApplyPatch(file = it, BMCLAPIMCBBSDownloader) }
        println("All files have been patched.")
    }

    private fun scanFile(): List<File> {
        return Files.walk(File("").toPath())
            .filter { it.toFile().isFile }
            .filter { it.toFile().name.endsWith(".jar") }
            .filter { file -> finders.any { it.shouldPatch(file.toFile()) } }
            .map { it.toFile() }
            .collect(Collectors.toList())
    }

    private fun findAndApplyPatch(file: File, downloader: Downloader) {
        finders.find { it.shouldPatch(file) }?.apply {
            this.getContext(file).also { context ->
                println("Found ${context::class.simpleName} with version ${context.version} in ${file.name}")
                val patchedSourceUrl = downloader.getDownloadLink(context.version)
                println("Patch sourceUrl from ${context.sourceUrl} to $patchedSourceUrl")
                try {
                    val patched = this.patch(file) {
                        it.sourceUrl = patchedSourceUrl
                    }
                    println("Successfully patched ${file.name} to ${patched.name}")
                } catch (e: Exception) {
                    println("Failed to path file caused by ${e.javaClass.name}: ${e.localizedMessage}")
                }
                println()
            }
        }
    }

}