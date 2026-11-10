package omni.toolbox.utils

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ArchiveUtils {
    fun zip(files: List<File>, outputZip: File) {
        ZipOutputStream(FileOutputStream(outputZip)).use { zos ->
            files.forEach { file ->
                if (file.isDirectory) {
                    zipDirectory(file, file.name, zos)
                } else {
                    zipFile(file, "", zos)
                }
            }
        }
    }

    private fun zipFile(file: File, parentPath: String, zos: ZipOutputStream) {
        val entryName = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
        val entry = ZipEntry(entryName)
        zos.putNextEntry(entry)
        FileInputStream(file).use { fis ->
            fis.copyTo(zos)
        }
        zos.closeEntry()
    }

    private fun zipDirectory(dir: File, parentPath: String, zos: ZipOutputStream) {
        val children = dir.listFiles()
        if (children.isNullOrEmpty()) {
            val entryName = if (parentPath.endsWith("/")) parentPath else "$parentPath/"
            zos.putNextEntry(ZipEntry(entryName))
            zos.closeEntry()
            return
        }
        children.forEach { file ->
            if (file.isDirectory) {
                zipDirectory(file, "$parentPath/${file.name}", zos)
            } else {
                zipFile(file, parentPath, zos)
            }
        }
    }

    fun unzip(zipFile: File, outputDir: File) {
        val canonicalDestDir = outputDir.canonicalFile
        if (!canonicalDestDir.exists()) canonicalDestDir.mkdirs()
        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(canonicalDestDir, entry.name)
                val canonicalOutPath = outFile.canonicalPath
                val canonicalDestPath = canonicalDestDir.canonicalPath
                if (!canonicalOutPath.startsWith(canonicalDestPath + File.separator) && canonicalOutPath != canonicalDestPath) {
                    throw SecurityException("Zip Slip vulnerability detected for entry: ${entry.name}")
                }
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    fun create7z(files: List<File>, output7z: File) {
        SevenZOutputFile(output7z).use { out ->
            files.forEach { file ->
                if (file.isDirectory) {
                    add7zDirectory(file, file.name, out)
                } else {
                    add7zFile(file, file.name, out)
                }
            }
        }
    }

    private fun add7zFile(file: File, entryPath: String, out: SevenZOutputFile) {
        val entry = out.createArchiveEntry(file, entryPath)
        out.putArchiveEntry(entry)
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                out.write(buffer, 0, bytesRead)
            }
        }
        out.closeArchiveEntry()
    }

    private fun add7zDirectory(dir: File, parentPath: String, out: SevenZOutputFile) {
        val children = dir.listFiles()
        if (children.isNullOrEmpty()) {
            val entryPath = if (parentPath.endsWith("/")) parentPath else "$parentPath/"
            val entry = out.createArchiveEntry(dir, entryPath)
            out.putArchiveEntry(entry)
            out.closeArchiveEntry()
            return
        }
        children.forEach { file ->
            val entryPath = "$parentPath/${file.name}"
            if (file.isDirectory) {
                add7zDirectory(file, entryPath, out)
            } else {
                add7zFile(file, entryPath, out)
            }
        }
    }

    fun extract7z(archive: File, destination: File) {
        val canonicalDestDir = destination.canonicalFile
        if (!canonicalDestDir.exists()) canonicalDestDir.mkdirs()
        SevenZFile.builder().setFile(archive).get().use { sevenZFile ->
            var entry = sevenZFile.nextEntry
            while (entry != null) {
                val outFile = File(canonicalDestDir, entry.name)
                val canonicalOutPath = outFile.canonicalPath
                val canonicalDestPath = canonicalDestDir.canonicalPath
                if (!canonicalOutPath.startsWith(canonicalDestPath + File.separator) && canonicalOutPath != canonicalDestPath) {
                    throw SecurityException("Zip Slip vulnerability detected for entry: ${entry.name}")
                }
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { out ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (sevenZFile.read(buffer).also { bytesRead = it } != -1) {
                            out.write(buffer, 0, bytesRead)
                        }
                    }
                }
                entry = sevenZFile.nextEntry
            }
        }
    }
}
