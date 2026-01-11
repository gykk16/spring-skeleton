package io.glory.common.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.longs.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class NumberExtTest : FunSpec({

    context("zeroOrOne") {
        test("should return 0 or 1") {
            repeat(100) {
                val result = 0.zeroOrOne()
                result shouldBeInRange 0..1
            }
        }
    }

    context("zeroOrOneOrTwo") {
        test("should return 0, 1, or 2") {
            repeat(100) {
                val result = 0.zeroOrOneOrTwo()
                result shouldBeInRange 0..2
            }
        }
    }

    context("randomBetween") {
        test("should return value within specified range") {
            repeat(100) {
                val result = 0.randomBetween(10, 20)
                result shouldBeInRange 10..20
            }
        }

        test("should return exact value when min equals max") {
            val result = 0.randomBetween(5, 5)
            result shouldBe 5
        }

        test("should work with negative numbers") {
            repeat(100) {
                val result = 0.randomBetween(-10, -5)
                result shouldBeInRange -10..-5
            }
        }
    }

    context("randomOfLength") {
        test("should return number with correct length for length 1") {
            repeat(50) {
                val result = 0.randomOfLength(1)
                result shouldBeInRange 1L..9L
            }
        }

        test("should return number with correct length for length 5") {
            repeat(50) {
                val result = 0.randomOfLength(5)
                result shouldBeInRange 10000L..99999L
            }
        }

        test("should return number with correct length for length 10") {
            repeat(50) {
                val result = 0.randomOfLength(10)
                result shouldBeInRange 1000000000L..9999999999L
            }
        }

        test("should throw exception for length less than 1") {
            val exception = shouldThrow<IllegalArgumentException> {
                0.randomOfLength(0)
            }
            exception.message shouldContain "Length must be between 1 and 10"
        }

        test("should throw exception for length greater than 10") {
            val exception = shouldThrow<IllegalArgumentException> {
                0.randomOfLength(11)
            }
            exception.message shouldContain "Length must be between 1 and 10"
        }
    }

    context("commaSeparated") {
        test("should format integer with comma separators") {
            1000.commaSeparated() shouldBe "1,000"
            1000000.commaSeparated() shouldBe "1,000,000"
            123456789.commaSeparated() shouldBe "123,456,789"
        }

        test("should format with decimal places") {
            1234.56.commaSeparated(2) shouldBe "1,234.56"
            1000.commaSeparated(2) shouldBe "1,000.00"
            1234567.89.commaSeparated(1) shouldBe "1,234,567.9"
        }

        test("should handle small numbers") {
            0.commaSeparated() shouldBe "0"
            999.commaSeparated() shouldBe "999"
            100.commaSeparated(2) shouldBe "100.00"
        }

        test("should handle negative numbers") {
            (-1000).commaSeparated() shouldBe "-1,000"
            (-1234.56).commaSeparated(2) shouldBe "-1,234.56"
        }

        test("should throw exception for negative position") {
            val exception = shouldThrow<IllegalArgumentException> {
                1000.commaSeparated(-1)
            }
            exception.message shouldContain "Position must be non-negative"
        }
    }
})
