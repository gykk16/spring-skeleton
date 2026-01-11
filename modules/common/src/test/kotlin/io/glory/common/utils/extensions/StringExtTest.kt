package io.glory.common.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class StringExtTest : FunSpec({

    context("ifNullOrBlank") {
        test("should return string itself when not null or blank") {
            "hello".ifNullOrBlank() shouldBe "hello"
            "hello".ifNullOrBlank("default") shouldBe "hello"
        }

        test("should return default when null") {
            val nullString: String? = null
            nullString.ifNullOrBlank() shouldBe ""
            nullString.ifNullOrBlank("default") shouldBe "default"
        }

        test("should return default when blank") {
            "".ifNullOrBlank() shouldBe ""
            "".ifNullOrBlank("default") shouldBe "default"
            "   ".ifNullOrBlank("default") shouldBe "default"
        }
    }

    context("removeAllSpaces") {
        test("should remove all spaces from string") {
            "hello world".removeAllSpaces() shouldBe "helloworld"
            "  hello  world  ".removeAllSpaces() shouldBe "helloworld"
            "no spaces".removeAllSpaces() shouldBe "nospaces"
        }

        test("should return empty string when only spaces") {
            "   ".removeAllSpaces() shouldBe ""
        }

        test("should return same string when no spaces") {
            "hello".removeAllSpaces() shouldBe "hello"
        }
    }

    context("maskDefault") {
        test("should return **** for strings with 4 or fewer characters") {
            "".maskDefault() shouldBe "****"
            "a".maskDefault() shouldBe "****"
            "ab".maskDefault() shouldBe "****"
            "abc".maskDefault() shouldBe "****"
            "abcd".maskDefault() shouldBe "****"
        }

        test("should mask correctly for various lengths") {
            "12345".maskDefault() shouldBe "****5"
            "123456".maskDefault() shouldBe "****56"
            "1234567".maskDefault() shouldBe "****567"
            "12345678".maskDefault() shouldBe "****5678"
        }

        test("should mask middle characters for longer strings") {
            "123456789".maskDefault() shouldBe "1****6789"
            "1234567890".maskDefault() shouldBe "12****7890"
            "12345678901".maskDefault() shouldBe "123****8901"
            "123456789012".maskDefault() shouldBe "1234****9012"
        }

        test("should throw exception for blank string in strict mode") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskDefault(strict = true)
            }
            exception.message shouldContain "Input string cannot be blank"
        }
    }

    context("maskKoreanName") {
        test("should return *** for empty or single character") {
            "".maskKoreanName() shouldBe "***"
            "김".maskKoreanName() shouldBe "***"
        }

        test("should mask second character for two character names") {
            "김철".maskKoreanName() shouldBe "김*"
        }

        test("should mask middle characters for longer names") {
            "김철수".maskKoreanName() shouldBe "김*수"
            "김철수민".maskKoreanName() shouldBe "김**민"
            "남궁민수".maskKoreanName() shouldBe "남**수"
        }

        test("should throw exception for blank string in strict mode") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskKoreanName(strict = true)
            }
            exception.message shouldContain "Input string cannot be blank"
        }
    }

    context("maskPhone") {
        test("should mask phone numbers") {
            "01012345678".maskPhone() shouldBe "*******5678"
            "0101234567".maskPhone() shouldBe "******4567"
        }

        test("should return **** for short strings") {
            "1234".maskPhone() shouldBe "****"
        }

        test("should throw exception for blank string in strict mode") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskPhone(strict = true)
            }
            exception.message shouldContain "Input string cannot be blank"
        }
    }

    context("maskEmail") {
        test("should return **** for strings with 4 or fewer characters") {
            "a@b".maskEmail() shouldBe "****"
            "ab".maskEmail() shouldBe "****"
        }

        test("should mask email local part correctly") {
            "test@example.com".maskEmail() shouldBe "te***@example.com"
            "ab@domain.com".maskEmail() shouldBe "***@domain.com"
            "hello@world.com".maskEmail() shouldBe "he***@world.com"
        }

        test("should handle email without @ sign") {
            "testexample".maskEmail() shouldBe "*******mple"
        }

        test("should throw exception for blank string in strict mode") {
            val exception = shouldThrow<IllegalArgumentException> {
                "".maskEmail(strict = true)
            }
            exception.message shouldContain "Input string cannot be blank"
        }
    }

    context("mask") {
        test("should return **** for strings with 4 or fewer characters") {
            "".mask() shouldBe "****"
            "ab".mask() shouldBe "****"
            "abcd".mask() shouldBe "****"
        }

        test("should mask from start to end-4 by default") {
            "12345678".mask() shouldBe "****5678"
            "1234567890".mask() shouldBe "******7890"
        }

        test("should mask with custom start and end") {
            "1234567890".mask(2, 6) shouldBe "12****7890"
            "abcdefgh".mask(1, 5) shouldBe "a****fgh"
        }

        test("should return original string when start >= end") {
            "12345678".mask(5, 3) shouldBe "12345678"
        }

        test("should handle out of bounds indices safely") {
            "12345678".mask(-5, 100) shouldBe "********"
            "12345678".mask(0, 100) shouldBe "********"
        }
    }
})
