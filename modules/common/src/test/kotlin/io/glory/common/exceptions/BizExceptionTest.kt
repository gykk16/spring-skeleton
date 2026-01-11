package io.glory.common.exceptions

import io.glory.common.codes.response.ErrorCode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BizExceptionTest : DescribeSpec({

    describe("BizException") {

        context("when created with only ResponseCode") {
            it("should use ResponseCode message as exception message") {
                val exception = BizException(ErrorCode.NOT_FOUND)

                exception.code shouldBe ErrorCode.NOT_FOUND
                exception.message shouldBe ErrorCode.NOT_FOUND.message
                exception.cause shouldBe null
                exception.printStackTrace shouldBe false
            }
        }

        context("when created with custom message") {
            it("should override ResponseCode message") {
                val customMessage = "User with ID 123 not found"
                val exception = BizException(ErrorCode.NOT_FOUND, customMessage)

                exception.code shouldBe ErrorCode.NOT_FOUND
                exception.message shouldBe customMessage
            }
        }

        context("when created with cause") {
            it("should chain the cause exception") {
                val rootCause = IllegalArgumentException("Invalid input")
                val exception = BizException(
                    code = ErrorCode.INVALID_ARGUMENT,
                    cause = rootCause
                )

                exception.cause shouldBe rootCause
                exception.cause?.message shouldBe "Invalid input"
            }
        }

        context("when printStackTrace is enabled") {
            it("should set printStackTrace flag to true") {
                val exception = BizException(
                    code = ErrorCode.SERVER_ERROR,
                    printStackTrace = true
                )

                exception.printStackTrace shouldBe true
            }
        }

        it("should be a checked Exception") {
            val exception = BizException(ErrorCode.SERVER_ERROR)

            exception.shouldBeInstanceOf<Exception>()
        }
    }
})
