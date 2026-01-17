package io.glory.common.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class StringExtTest : FunSpec({

    context("ifNullOrBlank") {
        test("should return the string itself when not null or blank") {
            "hello".ifNullOrBlank() shouldBe "hello"
            "hello".ifNullOrBlank("default") shouldBe "hello"
        }

        test("should return default value when null") {
            val nullString: String? = null
            nullString.ifNullOrBlank() shouldBe ""
            nullString.ifNullOrBlank("default") shouldBe "default"
        }

        test("should return default value when blank") {
            "".ifNullOrBlank() shouldBe ""
            "".ifNullOrBlank("default") shouldBe "default"
            "   ".ifNullOrBlank("default") shouldBe "default"
        }
    }

    context("removeAllSpaces") {
        test("should remove all spaces from string") {
            "hello world".removeAllSpaces() shouldBe "helloworld"
            "  a  b  c  ".removeAllSpaces() shouldBe "abc"
            "no spaces".removeAllSpaces() shouldBe "nospaces"
        }

        test("should return empty string when input is only spaces") {
            "   ".removeAllSpaces() shouldBe ""
        }

        test("should return same string when no spaces") {
            "hello".removeAllSpaces() shouldBe "hello"
        }
    }

    context("maskDefault") {
        test("should return **** for strings with length 1 to 4") {
            "1".maskDefault() shouldBe "****"
            "12".maskDefault() shouldBe "****"
            "123".maskDefault() shouldBe "****"
            "1234".maskDefault() shouldBe "****"
        }

        test("should show last 1 char for length 5") {
            "12345".maskDefault() shouldBe "****5"
        }

        test("should show last 2 chars for length 6") {
            "123456".maskDefault() shouldBe "****56"
        }

        test("should show last 3 chars for length 7") {
            "1234567".maskDefault() shouldBe "****567"
        }

        test("should show last 4 chars for length 8") {
            "12345678".maskDefault() shouldBe "****5678"
        }

        test("should show first 1 + last 4 chars for length 9") {
            "123456789".maskDefault() shouldBe "1****6789"
        }

        test("should show first 2 + last 4 chars for length 10") {
            "1234567890".maskDefault() shouldBe "12****7890"
        }

        test("should show first 3 + last 4 chars for length 11") {
            "12345678901".maskDefault() shouldBe "123****8901"
        }

        test("should show first 4 + last 4 chars for length 12+") {
            "123456789012".maskDefault() shouldBe "1234****9012"
            "1234567890123".maskDefault() shouldBe "1234*****0123"
            "12345678901234".maskDefault() shouldBe "1234******1234"
        }

        test("should throw exception when strict and input is blank") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskDefault(strict = true)
            }
            exception.message shouldContain "blank"
        }

        test("should return **** when not strict and input is blank") {
            "".maskDefault(strict = false) shouldBe "****"
        }
    }

    context("maskKoreanName") {
        test("should return *** for empty or single char names") {
            "".maskKoreanName() shouldBe "***"
            "홍".maskKoreanName() shouldBe "***"
        }

        test("should show first char + * for 2-char names") {
            "홍길".maskKoreanName() shouldBe "홍*"
        }

        test("should mask middle chars for 3+ char names") {
            "홍길동".maskKoreanName() shouldBe "홍*동"
            "홍길동수".maskKoreanName() shouldBe "홍**수"
            "남궁민수".maskKoreanName() shouldBe "남**수"
        }

        test("should throw exception when strict and input is blank") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskKoreanName(strict = true)
            }
            exception.message shouldContain "blank"
        }
    }

    context("maskPhone") {
        test("should mask all digits except last 4") {
            "01012345678".maskPhone() shouldBe "*******5678"
            "01234567890".maskPhone() shouldBe "*******7890"
            "0101234".maskPhone() shouldBe "***1234"
        }

        test("should return **** for short phone numbers") {
            "010".maskPhone() shouldBe "****"
            "0101".maskPhone() shouldBe "****"
        }

        test("should throw exception when strict and input is blank") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskPhone(strict = true)
            }
            exception.message shouldContain "blank"
        }
    }

    context("maskEmail") {
        test("should mask local part showing first 2 chars") {
            "user@example.com".maskEmail() shouldBe "us***@example.com"
            "username@domain.co.kr".maskEmail() shouldBe "us******@domain.co.kr"
        }

        test("should return ***@domain when local part has 2 or fewer chars") {
            "ab@example.com".maskEmail() shouldBe "***@example.com"
            "a@example.com".maskEmail() shouldBe "***@example.com"
        }

        test("should fallback to mask when no valid @ found") {
            "invalidemail".maskEmail() shouldBe "********mail"
            "@domain.com".maskEmail() shouldBe "*******.com"
            "user@".maskEmail() shouldBe "*ser@"
        }

        test("should return **** for short emails") {
            "a@b".maskEmail() shouldBe "****"
            "ab".maskEmail() shouldBe "****"
        }

        test("should throw exception when strict and input is blank") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskEmail(strict = true)
            }
            exception.message shouldContain "blank"
        }
    }

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
    }
})
