package io.hikarilan.paperclippatcher

import com.google.gson.Gson
import java.io.File
import java.nio.file.Files
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

val finders = setOf(PropertiesDownloadContextFinder, JsonDownloadContextFinder, PlainTextDownloadContextFinder)

sealed interface DownloadContextFinder<T : DownloadContext<*>> {

    fun shouldPatch(file: File): Boolean

    fun getContext(file: File): T

    fun patch(file: File, modifier: (DownloadContext<*>) -> Unit): File

}

object PropertiesDownloadContextFinder : DownloadContextFinder<PropertiesDownloadContext> {

    override fun shouldPatch(file: File): Boolean {
        return file.isFile && file.exists()
                && ZipFile(file).getEntry("patch.properties") != null
                && ZipFile(file).getEntry("paperclipPatcher_patched") == null
    }

    override fun getContext(file: File): PropertiesDownloadContext {
        ZipFile(file).let { zipFile ->
            zipFile.getInputStream(zipFile.getEntry("patch.properties")).use {
                return PropertiesDownloadContext(it.readBytes())
            }
        }
    }

    override fun patch(file: File, modifier: (DownloadContext<*>) -> Unit): File {
        val patchedFile = Files.createFile(file.toPath().resolveSibling("patched_${file.name}")).toFile()
        ZipFile(file).let { zipFile ->
            val output = JarOutputStream(patchedFile.outputStream())
            zipFile.stream().filter { it.name != "patch.properties" }.forEach {
                output.putNextEntry(it)
                output.write(zipFile.getInputStream(it).readBytes())
                output.closeEntry()
            }

            output.putNextEntry(ZipEntry("patch.properties"))
            getContext(file).apply { modifier.invoke(this) }.apply().store(output, "")
            output.closeEntry()

            addPatchedInformation(output)

            output.close()
        }
        return patchedFile
    }

}

object JsonDownloadContextFinder : DownloadContextFinder<JsonDownloadContext> {

    override fun shouldPatch(file: File): Boolean {
        return file.isFile && file.exists()
                && ZipFile(file).getEntry("patch.json") != null
                && ZipFile(file).getEntry("paperclipPatcher_patched") == null
    }

    override fun getContext(file: File): JsonDownloadContext {
        ZipFile(file).let { zipFile ->
            zipFile.getInputStream(zipFile.getEntry("patch.json")).use {
                return JsonDownloadContext(it.readBytes())
            }
        }
    }

    override fun patch(file: File, modifier: (DownloadContext<*>) -> Unit): File {
        val patchedFile = Files.createFile(file.toPath().resolveSibling("patched_${file.name}")).toFile()
        ZipFile(file).let { zipFile ->
            val output = JarOutputStream(patchedFile.outputStream())
            zipFile.stream().filter { it.name != "patch.json" }.forEach {
                output.putNextEntry(it)
                output.write(zipFile.getInputStream(it).readBytes())
                output.closeEntry()
            }

            Gson().toJson(getContext(file).apply { modifier.invoke(this) }.apply()).toByteArray().let {
                output.putNextEntry(ZipEntry("patch.json"))
                output.write(it)
                output.closeEntry()
            }

            addPatchedInformation(output)

            output.close()
        }
        return patchedFile
    }

}

object PlainTextDownloadContextFinder : DownloadContextFinder<PlainTextDownloadContext> {

    override fun shouldPatch(file: File): Boolean {
        return file.isFile && file.exists()
                && ZipFile(file).getEntry("META-INF/download-context") != null
                && ZipFile(file).getEntry("version.json") != null
                && ZipFile(file).getEntry("paperclipPatcher_patched") == null
    }

    override fun getContext(file: File): PlainTextDownloadContext {
        ZipFile(file).let { zipFile ->
            val data = zipFile.getInputStream(zipFile.getEntry("META-INF/download-context"))
            val version = zipFile.getInputStream(zipFile.getEntry("version.json"))
            return PlainTextDownloadContext(data.use { it.readBytes() }, version.use { it.readBytes() })
        }
    }

    override fun patch(file: File, modifier: (DownloadContext<*>) -> Unit): File {
        val patchedFile = Files.createFile(file.toPath().resolveSibling("patched_${file.name}")).toFile()
        ZipFile(file).let { zipFile ->
            val output = JarOutputStream(patchedFile.outputStream())
            zipFile.stream().filter { it.name != "META-INF/download-context" }.forEach {
                output.putNextEntry(it)
                output.write(zipFile.getInputStream(it).readBytes())
                output.closeEntry()
            }

            getContext(file).apply { modifier.invoke(this) }.apply().toByteArray().let {
                output.putNextEntry(ZipEntry("META-INF/download-context"))
                output.write(it)
                output.closeEntry()
            }

            addPatchedInformation(output)

            output.close()
        }
        return patchedFile
    }

}

fun addPatchedInformation(outputStream: JarOutputStream) {
    outputStream.putNextEntry(ZipEntry("paperclipPatcher_patched"))
    outputStream.write("This file has been patched by PaperclipPatcher".toByteArray())
    outputStream.closeEntry()
}
