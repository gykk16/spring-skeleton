package io.glory.domain.holiday

import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.KnownException
import java.time.LocalDate

data class Holiday(
    val id: Long,
    val holidayDate: LocalDate,
    val name: String,
)

data class CreateHolidayRequest(
    val holidayDate: LocalDate,
    val name: String,
)

data class UpdateHolidayRequest(
    val holidayDate: LocalDate,
    val name: String,
)

class HolidayNotFoundException(id: Long) : KnownException(
    ErrorCode.DATA_NOT_FOUND,
    "Holiday not found: $id"
)
