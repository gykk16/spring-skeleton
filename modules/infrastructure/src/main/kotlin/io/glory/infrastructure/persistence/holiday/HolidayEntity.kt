package io.glory.infrastructure.persistence.holiday

import io.glory.infrastructure.persistence.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate

@Entity
@Table(
    name = "tb_holidays",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_holidays_01", columnNames = ["holiday_date", "name"])
    ]
)
class HolidayEntity(
    holidayDate: LocalDate,
    name: String,
    id: Long? = null,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    @Column(name = "holiday_date", nullable = false)
    var holidayDate: LocalDate = holidayDate
        private set

    @Column(name = "name", nullable = false, length = 100)
    var name: String = name
        private set

    fun update(holidayDate: LocalDate, name: String) {
        this.holidayDate = holidayDate
        this.name = name
    }

    companion object {
        fun create(holidayDate: LocalDate, name: String) = HolidayEntity(
            holidayDate = holidayDate,
            name = name,
        )
    }
}
