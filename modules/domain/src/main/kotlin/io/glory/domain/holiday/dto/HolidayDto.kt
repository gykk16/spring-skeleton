package io.glory.domain.holiday.dto

import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.KnownException
import io.glory.domain.holiday.entity.Holiday
import java.time.LocalDate

data class HolidayInfo(
    val id: Long,
    val holidayDate: LocalDate,
    val name: String,
) {
    companion object {
        fun from(entity: Holiday) = HolidayInfo(
            id = entity.id!!,
            holidayDate = entity.holidayDate,
            name = entity.name,
        )
    }
}

data class CreateHolidayRequest(
    val holidayDate: LocalDate,
    val name: String,
)

data class UpdateHolidayRequest(
    val holidayDate: LocalDate,
    val name: String,
)

