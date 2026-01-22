package io.glory.common.utils.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MaskingExtTest : FunSpec({

    context("mask") {
        test("should mask between start and end indices") {
            "1234567890".mask(2, 6) shouldBe "12****7890"
            "1234567890".mask(0, 4) shouldBe "****567890"
            "1234567890".mask(6, 10) shouldBe "123456****"
        }

        test("should use default end index (length - 4)") {
            "1234567890".mask(0) shouldBe "******7890"
            "12345678".mask(0) shouldBe "****5678"
        }

        test("should return **** for strings with length 4 or less") {
            "1234".mask() shouldBe "****"
            "123".mask() shouldBe "****"
            "12".mask() shouldBe "****"
            "1".mask() shouldBe "****"
        }

        test("should handle out-of-bound indices safely") {
            "1234567890".mask(-5, 5) shouldBe "*****67890"
            "1234567890".mask(5, 20) shouldBe "12345*****"
        }

        test("should return original string when start >= end after coercion") {
            "1234567890".mask(5, 5) shouldBe "1234567890"
            "1234567890".mask(8, 5) shouldBe "1234567890"
        }

        test("should use custom mask character") {
            "1234567890".mask(0, 6, '#') shouldBe "######7890"
        }
    }

    context("maskDigits") {
        test("should mask all digits except last 4") {
            "01012345678".maskDigits() shouldBe "*******5678"
            "1234567890".maskDigits() shouldBe "******7890"
        }

        test("should preserve non-digit characters") {
            "+82 10-1234-5678".maskDigits() shouldBe "+** **-****-5678"
            "010-1234-5678".maskDigits() shouldBe "***-****-5678"
            "1234-5678-9012-3456".maskDigits() shouldBe "****-****-****-3456"
        }

        test("should return original string when digit count <= visible digits") {
            "1234".maskDigits() shouldBe "1234"
            "123".maskDigits() shouldBe "123"
            "12-34".maskDigits() shouldBe "12-34"
        }

        test("should support custom visible digits") {
            "1234567890".maskDigits(2) shouldBe "********90"
            "010-1234-5678".maskDigits(2) shouldBe "***-****-**78"
        }

        test("should use custom mask character") {
            "1234567890".maskDigits(4, '#') shouldBe "######7890"
        }
    }

    context("maskName") {
        test("should return *** for empty or single char names") {
            "".maskName() shouldBe "***"
            "홍".maskName() shouldBe "***"
            "A".maskName() shouldBe "***"
        }

        test("should show first char + * for 2-char names") {
            "홍길".maskName() shouldBe "홍*"
            "AB".maskName() shouldBe "A*"
        }

        test("should mask middle chars for 3+ char names") {
            "홍길동".maskName() shouldBe "홍*동"
            "홍길동수".maskName() shouldBe "홍**수"
            "John".maskName() shouldBe "J**n"
            "Alice".maskName() shouldBe "A***e"
        }

        test("should use custom mask character") {
            "홍길동".maskName('#') shouldBe "홍#동"
        }
    }

    context("maskEmail") {
        test("should mask local part showing first 2 chars") {
            "user@example.com".maskEmail() shouldBe "us**@example.com"
            "username@domain.co.kr".maskEmail() shouldBe "us******@domain.co.kr"
        }

        test("should return original string when local part is too short") {
            "ab@example.com".maskEmail() shouldBe "ab@example.com"
            "a@example.com".maskEmail() shouldBe "a@example.com"
        }

        test("should return original string when no valid @ found") {
            "invalidemail".maskEmail() shouldBe "invalidemail"
            "@domain.com".maskEmail() shouldBe "@domain.com"
            "user@".maskEmail() shouldBe "user@"
        }

        test("should support custom visible chars") {
            "username@example.com".maskEmail(3) shouldBe "use*****@example.com"
            "username@example.com".maskEmail(4) shouldBe "user****@example.com"
        }

        test("should use custom mask character") {
            "user@example.com".maskEmail(2, '#') shouldBe "us##@example.com"
        }
    }

})
