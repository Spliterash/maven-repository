package ru.spliterash.mavenrepository.repository

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component
import ru.spliterash.mavenrepository.PUBLIC
import ru.spliterash.mavenrepository.RESERVED_NAMES
import ru.spliterash.mavenrepository.auth.NotAuthException
import ru.spliterash.mavenrepository.repository.exceptions.InvalidRepositoryNameException
import ru.spliterash.mavenrepository.repository.exceptions.NotFoundException
import ru.spliterash.mavenrepository.repository.result.*
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.io.InputStream

@Component
class MavenRepositoryService(
    private val config: RepoConfig
) {
    private final val repositoryFolder = File(config.directory)

    init {
        repositoryFolder.mkdirs()
    }

    fun repoName(path: String) = path.substring(1).substringBefore("/")

    fun saveFile(path: String, stream: InputStream) {
        val repoName = repoName(path)

        if (RESERVED_NAMES.contains(repoName))
            throw InvalidRepositoryNameException()

        val file = File(repositoryFolder, path)
        file.parentFile.mkdirs()
        FileOutputStream(file).use {
            IOUtils.copy(stream, it)
        }
    }

    fun readFile(path: String, auth: Boolean): QueryResult {
        if (path == "/" && !auth) {
            return FolderResult(listOf(FileInfo("group", 0, FileInfo.Type.DIRECTORY)))
        }

        if (path == "/maven-metadata.xml")
            throw NotFoundException()

        val repoName = repoName(path)

        if (repoName != PUBLIC && !auth)
            throw NotAuthException()

        // Если это прямое обращение, просто найдём в папке
        if (!RESERVED_NAMES.contains(repoName)) {
            val file = File(repositoryFolder, path)

            if (file.isFile)
                return FileResult(file)
            else if (file.isDirectory) {
                val directories = file.listFiles().mapTo(arrayListOf()) { it.info }
                if (path == "/") {
                    directories.add(0, FileInfo("public", 0, FileInfo.Type.DIRECTORY))
                    directories.add(0, FileInfo("all", 0, FileInfo.Type.DIRECTORY))
                }

                return FolderResult(directories)
            }
        }


        val searchInRepos: List<String> = if (repoName == PUBLIC)
            config.public
        else
            repositoryFolder.listFiles(FileFilter { it.isDirectory })?.map { it.name } ?: listOf()
        if (searchInRepos.isEmpty())
            throw NotFoundException()

        var fileRelativePath = path.substring(repoName.length + 1)
        if (fileRelativePath.isEmpty())
            fileRelativePath = "."

        var files: MutableSet<FileInfo>? = null
        for (repo in searchInRepos) {
            val repoFolder = File(repositoryFolder, repo)
            val file = File(repoFolder, fileRelativePath)

            if (file.isFile)
                return FileResult(file)
            else if (file.isDirectory) {
                if (files == null)
                    files = linkedSetOf()
                for (listFile in file.listFiles()!!) {
                    files += listFile.info
                }
            }
        }
        return if (files == null)
            FolderResult(listOf())
        else
            FolderResult(files)
    }
}