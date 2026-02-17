package io.glory.domain.holiday.application

import io.glory.domain.holiday.dto.HolidayInfo
import io.glory.domain.holiday.service.HolidayService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class HolidayQueryApplication(
    private val holidayService: HolidayService,
) {

    fun findPageByYear(year: Int, pageable: Pageable): Page<HolidayInfo> {
        return holidayService.findPageByYear(year, pageable)
    }

    fun findPageByYearAndMonth(year: Int, month: Int, pageable: Pageable): Page<HolidayInfo> {
        return holidayService.findPageByYearAndMonth(year, month, pageable)
    }

    fun findByDate(year: Int, month: Int, day: Int): List<HolidayInfo> {
        return holidayService.findByDate(year, month, day)
    }

    fun findById(id: Long): HolidayInfo {
        return holidayService.findById(id)
    }
}
