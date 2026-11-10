package omni.toolbox.utils

import java.io.File

object NotionChunker {
    /**
     * Splits a list of items into chunks of 10 for Notion API compliance.
     */
    fun <T> chunk(items: List<T>, chunkSize: Int = 10): List<List<T>> {
        return items.chunked(chunkSize)
    }

    /**
     * Iteratively scans a directory and returns all non-directory files.
     */
    fun scanDirectory(directory: File): List<File> {
        val files = mutableListOf<File>()
        val queue = ArrayDeque<File>()
        queue.add(directory)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val children = current.listFiles() ?: continue
            for (child in children) {
                if (child.isDirectory) {
                    queue.addLast(child)
                } else {
                    files.add(child)
                }
            }
        }
        return files
    }
}
