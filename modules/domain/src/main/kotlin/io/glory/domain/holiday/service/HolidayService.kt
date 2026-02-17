package io.glory.domain.holiday.service

import io.glory.domain.holiday.dto.CreateHolidayRequest
import io.glory.domain.holiday.dto.HolidayInfo
import io.glory.domain.holiday.dto.UpdateHolidayRequest
import io.glory.domain.holiday.entity.Holiday
import io.glory.domain.holiday.exception.HolidayError
import io.glory.domain.holiday.exception.HolidayException
import io.glory.domain.holiday.exception.HolidayNotFoundException
import io.glory.domain.holiday.repository.HolidayJpaRepository
import io.glory.domain.holiday.repository.HolidayQueryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HolidayService(
    private val holidayJpaRepository: HolidayJpaRepository,
    private val holidayQueryRepository: HolidayQueryRepository,
) {

    fun findPageByYear(year: Int, pageable: Pageable): Page<HolidayInfo> {
        return holidayQueryRepository.fetchPageByYear(year, pageable)
    }

    fun findPageByYearAndMonth(year: Int, month: Int, pageable: Pageable): Page<HolidayInfo> {
        return holidayQueryRepository.fetchPageByYearAndMonth(year, month, pageable)
    }

    fun findByDate(year: Int, month: Int, day: Int): List<HolidayInfo> {
        val date = LocalDate.of(year, month, day)
        return holidayJpaRepository.findByHolidayDate(date)
            .map { HolidayInfo.from(it) }
    }

    fun findById(id: Long): HolidayInfo {
        return holidayJpaRepository.findById(id)
            .map { HolidayInfo.from(it) }
            .orElseThrow { HolidayNotFoundException(id) }
    }

    fun existsByDateAndName(holidayDate: LocalDate, name: String): Boolean {
        return holidayJpaRepository.existsByHolidayDateAndName(holidayDate, name)
    }

    fun create(request: CreateHolidayRequest): HolidayInfo {
        if (existsByDateAndName(request.holidayDate, request.name)) {
            throw HolidayException(
                HolidayError.HOLIDAY_ALREADY_EXISTS,
                "Holiday already exists: ${request.holidayDate} ${request.name}"
            )
        }
        val entity = Holiday.create(
            holidayDate = request.holidayDate,
            name = request.name,
        )
        return HolidayInfo.from(holidayJpaRepository.save(entity))
    }

    fun createAll(requests: List<CreateHolidayRequest>): List<HolidayInfo> {
        val entities = requests.map {
            Holiday.create(
                holidayDate = it.holidayDate,
                name = it.name,
            )
        }
        return holidayJpaRepository.saveAll(entities).map { HolidayInfo.from(it) }
    }

    fun update(id: Long, request: UpdateHolidayRequest): HolidayInfo {
        val entity = holidayJpaRepository.findById(id)
            .orElseThrow { HolidayNotFoundException(id) }

        entity.update(
            holidayDate = request.holidayDate,
            name = request.name,
        )
        return HolidayInfo.from(entity)
    }

    fun delete(id: Long) {
        if (!holidayJpaRepository.existsById(id)) {
            throw HolidayNotFoundException(id)
        }
        holidayJpaRepository.deleteById(id)
    }
}
