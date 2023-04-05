package ru.spliterash.mavenrepository.repository.result

import java.io.File

sealed interface QueryResult

val File.type
    get() = if (this.isFile) FileInfo.Type.FILE
    else if (this.isDirectory) FileInfo.Type.DIRECTORY
    else throw IllegalArgumentException()
val File.info
    get() = FileInfo(name, if (this.isFile) length() else 0, type)

class FolderResult(
    val files: Collection<FileInfo>
) : QueryResult

data class FileInfo(
    val name: String,
    val size: Long,
    val type: Type
) {
    enum class Type {
        FILE, DIRECTORY
    }
}

class FileResult(
    private val file: File
) : QueryResult {

    val info: FileInfo
        get() = file.info

    fun openStream() = file.inputStream()
}