package com.github.old_horizon.selenium

import com.codeborne.selenide.Condition.exactText
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.WebDriverRunner
import com.thoughtworks.gauge.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import java.util.*
import java.util.concurrent.TimeUnit

class Steps {
    private val props = loadProperties()
    private val website = Website(props.getProperty("website.port").toInt())
    private val downloadDir = lazy { DownloadDirectory.of(WebDriverRunner.driver()) }

    @BeforeSuite
    fun beforeSuite() {
        loadProperties()
        website.setup()
    }

    @AfterSuite
    fun afterSuite() {
        website.tearDown()
    }

    @BeforeScenario
    @AfterScenario
    fun cleanDownloadDirectory() {
        if (downloadDir.isInitialized()) {
            downloadDir.value.deleteFiles()
        }
    }

    @Step("Navigate to <path>")
    fun navigate(path: String) {
        open(path)
    }

    @Step("Click <name> link")
    fun clickLink(name: String) {
        `$$`("a").find(exactText(name)).click()
        TimeUnit.SECONDS.sleep(1)
    }

    @Step("File <name> has downloaded")
    fun hasDownloaded(name: String) {
        downloadDir.value.exists(name) shouldBe true
    }

    @Step("Content of downloaded <actual> equals to <expected>")
    fun contentEquals(actual: String, expected: String) {
        downloadDir.value.inputStream(actual).readAllBytes() shouldBeEqualTo
                read(expected).readAllBytes()
    }

    private fun loadProperties(): Properties {
        read("uat.properties").use {
            val props = Properties().apply { load(it) }
            props.forEach { System.setProperty(it.key as String, it.value as String) }
            return props
        }
    }

    private fun read(name: String) = javaClass.classLoader.getResourceAsStream(name)!!
}