package io.glory.commonapiapp.api.dto

import io.glory.domain.holiday.Holiday
import java.time.LocalDate

data class CreateHolidayApiRequest(
    val holidayDate: LocalDate,
    val name: String,
)

data class UpdateHolidayApiRequest(
    val holidayDate: LocalDate,
    val name: String,
)

data class BulkCreateHolidayApiRequest(
    val holidays: List<CreateHolidayApiRequest>,
)

data class HolidayDto(
    val id: Long,
    val holidayDate: LocalDate,
    val name: String,
) {
    companion object {
        fun from(holiday: Holiday) = HolidayDto(
            id = holiday.id,
            holidayDate = holiday.holidayDate,
            name = holiday.name,
        )
    }
}

data class HolidaysResponse(
    val holidays: List<HolidayItem>,
) {
    companion object {
        fun from(holidays: List<Holiday>) = HolidaysResponse(
            holidays = holidays.map { HolidayItem(it.holidayDate, it.name) }
        )
    }
}

data class HolidayItem(
    val date: LocalDate,
    val name: String,
)
