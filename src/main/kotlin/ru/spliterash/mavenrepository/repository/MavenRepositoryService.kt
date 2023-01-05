package ru.spliterash.mavenrepository.repository

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component
import ru.spliterash.mavenrepository.PUBLIC
import ru.spliterash.mavenrepository.RESERVED_NAMES
import ru.spliterash.mavenrepository.repository.exceptions.InvalidRepositoryNameException
import java.io.*

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

    fun readFile(path: String): InputStream? {
        val repoName = repoName(path)

        // Если это прямое обращение, просто найдём в папке
        if (!RESERVED_NAMES.contains(repoName)) {
            val file = File(repositoryFolder, path)
            return if (!file.isFile)
                null
            else
                FileInputStream(file)
        }


        val searchInRepos: List<String> = if (repoName == PUBLIC)
            config.public
        else
            repositoryFolder.listFiles(FileFilter { it.isDirectory })?.map { it.name } ?: listOf()
        if (searchInRepos.isEmpty())
            return null

        val fileRelativePath = path.substring(1).substringAfter("/")

        for (repo in searchInRepos) {
            val repoFolder = File(repositoryFolder, repo)
            val file = File(repoFolder, fileRelativePath)

            if (file.isFile)
                return FileInputStream(file)
        }
        return null
    }
}