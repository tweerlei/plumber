package de.tweerlei.plumber.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PrefixExtensionsTest {

    @Test
    fun testExtractCommonPrefix() {
        extractCommonPrefix("", "").shouldBe("")
        extractCommonPrefix("hello", "").shouldBe("")
        extractCommonPrefix("", "hello").shouldBe("")
        extractCommonPrefix("hello", "yello").shouldBe("")
        extractCommonPrefix("yello", "hello").shouldBe("")
        extractCommonPrefix("hello", "hello2").shouldBe("hello")
        extractCommonPrefix("hello2", "hello").shouldBe("hello")
        extractCommonPrefix("hello1", "hello2").shouldBe("hello")
    }
}
