package io.glory.common.exceptions

import io.glory.common.codes.response.ErrorCode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class KnownExceptionTest : DescribeSpec({

    describe("KnownException") {

        context("when created") {
            it("should always have printStackTrace set to false") {
                val exception = KnownException(ErrorCode.DATA_NOT_FOUND)

                exception.printStackTrace shouldBe false
            }
        }

        context("when created with only ResponseCode") {
            it("should use ResponseCode message as exception message") {
                val exception = KnownException(ErrorCode.DATA_NOT_FOUND)

                exception.code shouldBe ErrorCode.DATA_NOT_FOUND
                exception.message shouldBe ErrorCode.DATA_NOT_FOUND.message
                exception.cause shouldBe null
            }
        }

        context("when created with custom message") {
            it("should override ResponseCode message") {
                val customMessage = "User profile not found"
                val exception = KnownException(ErrorCode.DATA_NOT_FOUND, customMessage)

                exception.code shouldBe ErrorCode.DATA_NOT_FOUND
                exception.message shouldBe customMessage
            }
        }

        context("when created with cause") {
            it("should chain the cause but still not print stack trace") {
                val rootCause = NoSuchElementException("Element missing")
                val exception = KnownException(
                    code = ErrorCode.DATA_NOT_FOUND,
                    cause = rootCause
                )

                exception.cause shouldBe rootCause
                exception.printStackTrace shouldBe false
            }
        }

        it("should be a BizRuntimeException") {
            val exception = KnownException(ErrorCode.DATA_NOT_FOUND)

            exception.shouldBeInstanceOf<BizRuntimeException>()
        }
    }
})
