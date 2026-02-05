package io.glory.infrastructure.persistence.holiday

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface HolidayRepository : JpaRepository<HolidayEntity, Long> {

    fun findByHolidayDate(holidayDate: LocalDate): List<HolidayEntity>

    @Query(
        """
        select h
          from HolidayEntity h
         where year(h.holidayDate) = :year
         order by h.holidayDate
        """
    )
    fun findByYear(year: Int): List<HolidayEntity>

    @Query(
        """
        select h
          from HolidayEntity h
         where year(h.holidayDate) = :year
           and month(h.holidayDate) = :month
         order by h.holidayDate
        """
    )
    fun findByYearAndMonth(year: Int, month: Int): List<HolidayEntity>

    fun existsByHolidayDateAndName(holidayDate: LocalDate, name: String): Boolean
}
