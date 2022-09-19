package de.tweerlei.plumber.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Duration

class FormattingExtensionsTest {

    @Test
    fun testHumanReadableNumber() {
        1_549_000_000.humanReadable().shouldBe("1.55G")
        1_000_000_000.humanReadable().shouldBe("1.00G")
        999_999_999.humanReadable().shouldBe("1000.00M")
        999_994_999.humanReadable().shouldBe("999.99M")
        1_000_000.humanReadable().shouldBe("1.00M")
        999_999.humanReadable().shouldBe("1000.00k")
        999_994.humanReadable().shouldBe("999.99k")
        1_000.humanReadable().shouldBe("1.00k")
        999.humanReadable().shouldBe("999.00")
        1.humanReadable().shouldBe("1.00")
        0.humanReadable().shouldBe("0.00")
        (-1).humanReadable().shouldBe("-1.00")
        (-1000).humanReadable().shouldBe("-1000.00")
    }

    @Test
    fun testHumanReadableDuration() {
        Duration.ofMillis(86399_999).humanReadable().shouldBe("23:59:59.999")
        Duration.ofMillis(3661_001).humanReadable().shouldBe("1:01:01.001")
        Duration.ofMillis(1).humanReadable().shouldBe("0:00:00.001")
        Duration.ofMillis(0).humanReadable().shouldBe("0:00:00.000")
    }
}
