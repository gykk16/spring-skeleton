package io.glory.domain.holiday.exception

import io.glory.common.exceptions.KnownException

open class HolidayException(
    code: HolidayError,
    message: String = code.message,
) : KnownException(
    code = code,
    message = message,
)

class HolidayNotFoundException(id: Long) : HolidayException(
    code = HolidayError.HOLIDAY_NOT_FOUND,
    message = "Holiday not found: $id",
)
