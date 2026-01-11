package io.glory.common.utils.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DateTimeExtTest : FunSpec({

    context("isToday") {
        test("should return true for today's date") {
            val today = LocalDate.now()
            today.isToday() shouldBe true
        }

        test("should return false for yesterday's date") {
            val yesterday = LocalDate.now().minusDays(1)
            yesterday.isToday() shouldBe false
        }

        test("should return false for tomorrow's date") {
            val tomorrow = LocalDate.now().plusDays(1)
            tomorrow.isToday() shouldBe false
        }
    }

    context("isYesterday") {
        test("should return true for yesterday's date") {
            val yesterday = LocalDate.now().minusDays(1)
            yesterday.isYesterday() shouldBe true
        }

        test("should return false for today's date") {
            val today = LocalDate.now()
            today.isYesterday() shouldBe false
        }

        test("should return false for two days ago") {
            val twoDaysAgo = LocalDate.now().minusDays(2)
            twoDaysAgo.isYesterday() shouldBe false
        }
    }

    context("isTomorrow") {
        test("should return true for tomorrow's date") {
            val tomorrow = LocalDate.now().plusDays(1)
            tomorrow.isTomorrow() shouldBe true
        }

        test("should return false for today's date") {
            val today = LocalDate.now()
            today.isTomorrow() shouldBe false
        }

        test("should return false for day after tomorrow") {
            val dayAfterTomorrow = LocalDate.now().plusDays(2)
            dayAfterTomorrow.isTomorrow() shouldBe false
        }
    }

    context("isPast") {
        test("should return true for past dates") {
            val pastDate = LocalDate.now().minusDays(1)
            pastDate.isPast() shouldBe true
        }

        test("should return false for today") {
            val today = LocalDate.now()
            today.isPast() shouldBe false
        }

        test("should return false for future dates") {
            val futureDate = LocalDate.now().plusDays(1)
            futureDate.isPast() shouldBe false
        }
    }

    context("isFuture") {
        test("should return true for future dates") {
            val futureDate = LocalDate.now().plusDays(1)
            futureDate.isFuture() shouldBe true
        }

        test("should return false for today") {
            val today = LocalDate.now()
            today.isFuture() shouldBe false
        }

        test("should return false for past dates") {
            val pastDate = LocalDate.now().minusDays(1)
            pastDate.isFuture() shouldBe false
        }
    }

    context("getAge") {
        test("should return correct age for birthdate") {
            val birthDate = LocalDate.of(1990, 5, 15)
            val targetDate = LocalDate.of(2025, 5, 15)
            birthDate.getAge(targetDate) shouldBe 35
        }

        test("should return age minus one if birthday has not occurred yet") {
            val birthDate = LocalDate.of(1990, 12, 25)
            val targetDate = LocalDate.of(2025, 6, 1)
            birthDate.getAge(targetDate) shouldBe 34
        }

        test("should return 0 for same year birth") {
            val birthDate = LocalDate.of(2025, 1, 1)
            val targetDate = LocalDate.of(2025, 6, 1)
            birthDate.getAge(targetDate) shouldBe 0
        }

        test("should return correct age using current date") {
            val today = LocalDate.now()
            val birthDate = today.minusYears(30)
            birthDate.getAge() shouldBe 30
        }
    }
})
