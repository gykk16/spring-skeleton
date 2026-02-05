package io.glory.commonapplication.service

import io.glory.domain.holiday.CreateHolidayRequest
import io.glory.domain.holiday.Holiday
import io.glory.domain.holiday.HolidayNotFoundException
import io.glory.domain.holiday.UpdateHolidayRequest
import io.glory.infrastructure.persistence.holiday.HolidayEntity
import io.glory.infrastructure.persistence.holiday.HolidayRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class HolidayService(
    private val holidayRepository: HolidayRepository,
) {

    fun findByYear(year: Int): List<Holiday> {
        return holidayRepository.findByYear(year)
            .map { it.toHoliday() }
    }

    fun findByYearAndMonth(year: Int, month: Int): List<Holiday> {
        return holidayRepository.findByYearAndMonth(year, month)
            .map { it.toHoliday() }
    }

    fun findByDate(year: Int, month: Int, day: Int): List<Holiday> {
        val date = LocalDate.of(year, month, day)
        return holidayRepository.findByHolidayDate(date)
            .map { it.toHoliday() }
    }

    fun findById(id: Long): Holiday {
        return holidayRepository.findById(id)
            .map { it.toHoliday() }
            .orElseThrow { HolidayNotFoundException(id) }
    }

    @Transactional
    fun create(request: CreateHolidayRequest): Holiday {
        val entity = HolidayEntity.create(
            holidayDate = request.holidayDate,
            name = request.name,
        )
        return holidayRepository.save(entity).toHoliday()
    }

    @Transactional
    fun createAll(requests: List<CreateHolidayRequest>): List<Holiday> {
        val entities = requests.map {
            HolidayEntity.create(
                holidayDate = it.holidayDate,
                name = it.name,
            )
        }
        return holidayRepository.saveAll(entities).map { it.toHoliday() }
    }

    @Transactional
    fun update(id: Long, request: UpdateHolidayRequest): Holiday {
        val entity = holidayRepository.findById(id)
            .orElseThrow { HolidayNotFoundException(id) }

        entity.update(
            holidayDate = request.holidayDate,
            name = request.name,
        )
        return entity.toHoliday()
    }

    @Transactional
    fun delete(id: Long) {
        if (!holidayRepository.existsById(id)) {
            throw HolidayNotFoundException(id)
        }
        holidayRepository.deleteById(id)
    }

    private fun HolidayEntity.toHoliday() = Holiday(
        id = id!!,
        holidayDate = holidayDate,
        name = name,
    )
}
