package io.glory.common.exceptions

import io.glory.common.codes.response.ErrorCode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BizRuntimeExceptionTest : DescribeSpec({

    describe("BizRuntimeException") {

        context("when created with only ResponseCode") {
            it("should use ResponseCode message as exception message") {
                val exception = BizRuntimeException(ErrorCode.UNAUTHORIZED)

                exception.code shouldBe ErrorCode.UNAUTHORIZED
                exception.message shouldBe ErrorCode.UNAUTHORIZED.message
                exception.cause shouldBe null
                exception.printStackTrace shouldBe false
            }
        }

        context("when created with custom message") {
            it("should override ResponseCode message") {
                val customMessage = "Token expired at 2024-01-01"
                val exception = BizRuntimeException(ErrorCode.UNAUTHORIZED, customMessage)

                exception.code shouldBe ErrorCode.UNAUTHORIZED
                exception.message shouldBe customMessage
            }
        }

        context("when created with cause") {
            it("should chain the cause exception") {
                val rootCause = NullPointerException("Data was null")
                val exception = BizRuntimeException(
                    code = ErrorCode.ILLEGAL_STATE,
                    cause = rootCause
                )

                exception.cause shouldBe rootCause
                exception.cause?.message shouldBe "Data was null"
            }
        }

        context("when used for exception translation") {
            it("should wrap original exception with business context") {
                val dbException = RuntimeException("Connection timeout")
                val exception = BizRuntimeException(
                    code = ErrorCode.DB_ACCESS_ERROR,
                    message = "Failed to save user data",
                    cause = dbException,
                    printStackTrace = true
                )

                exception.code shouldBe ErrorCode.DB_ACCESS_ERROR
                exception.message shouldBe "Failed to save user data"
                exception.cause shouldBe dbException
                exception.printStackTrace shouldBe true
            }
        }

        it("should be an unchecked RuntimeException") {
            val exception = BizRuntimeException(ErrorCode.SERVER_ERROR)

            exception.shouldBeInstanceOf<RuntimeException>()
        }
    }
})
