package com.github.old_horizon.selenium

import com.codeborne.selenide.Driver
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import org.openqa.selenium.remote.SessionId
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.net.URL

interface DownloadDirectory {
    fun listFiles(): List<String>
    fun deleteFiles()
    fun exists(name: String): Boolean
    fun inputStream(name: String): InputStream
    fun deleteFile(name: String)

    companion object {
        fun of(driver: Driver): DownloadDirectory =
                driver.config().remote()
                        ?.let { RemoteDownloadDirectory(URL(it), driver.sessionId) }
                        ?: LocalDownloadDirectory(driver.browserDownloadsFolder()!!.toFile())
    }
}

class LocalDownloadDirectory(private val dir: File) : DownloadDirectory {
    override fun listFiles(): List<String> = dir.list()!!.toList()

    override fun deleteFiles() {
        dir.deleteRecursively()
        dir.mkdir()
    }

    override fun exists(name: String): Boolean = dir.resolve(name).exists()

    override fun inputStream(name: String): InputStream = dir.resolve(name).inputStream()

    override fun deleteFile(name: String) {
        dir.resolve(name).delete()
    }
}

class RemoteDownloadDirectory(private val remoteUrl: URL, private val sessionId: SessionId) : DownloadDirectory {
    override fun listFiles(): List<String> = buildUrl("/").httpGet()
            .responseObject<FilesJson>()
            .let(::handleResult).files
            .map { it.name }

    override fun deleteFiles() {
        buildUrl("/").httpDelete().response().let(::handleResult)
    }

    override fun exists(name: String): Boolean = listFiles().contains(name)

    override fun inputStream(name: String): InputStream =
            buildUrl("/$name").httpGet().response().let(::handleResult).let(::ByteArrayInputStream)

    override fun deleteFile(name: String) {
        buildUrl("/$name").httpDelete().response().let(::handleResult)
    }

    private fun buildUrl(path: String): String =
            "${remoteUrl.protocol}://${remoteUrl.host}:${remoteUrl.port}/grid/admin/Downloads/$sessionId$path"

    private fun <T : Any> handleResult(responseResult: ResponseResultOf<T>): T {
        val (_, _, result) = responseResult
        return when (result) {
            is Result.Success<T> -> result.get()
            is Result.Failure -> throw result.getException()
        }
    }

    data class FilesJson(val files: List<FileJson>)

    data class FileJson(val name: String)
}
