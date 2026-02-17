package io.glory.domain.holiday.application

import io.glory.domain.holiday.dto.CreateHolidayRequest
import io.glory.domain.holiday.dto.HolidayInfo
import io.glory.domain.holiday.dto.UpdateHolidayRequest
import io.glory.domain.holiday.service.HolidayService
import io.glory.domain.notification.HolidayNotificationEventFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HolidayCommandApplication(
    private val holidayService: HolidayService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    fun create(request: CreateHolidayRequest): HolidayInfo {
        val createdHoliday = holidayService.create(request)
        val event = HolidayNotificationEventFactory.holidayCreated(
            createdHoliday.id, createdHoliday.holidayDate, createdHoliday.name
        )
        applicationEventPublisher.publishEvent(event)
        return createdHoliday
    }

    fun createAll(requests: List<CreateHolidayRequest>): List<HolidayInfo> {
        val created = holidayService.createAll(requests)
        applicationEventPublisher.publishEvent(
            HolidayNotificationEventFactory.holidayBulkCreated(created.size)
        )
        return created
    }

    fun update(id: Long, request: UpdateHolidayRequest): HolidayInfo {
        val updated = holidayService.update(id, request)
        applicationEventPublisher.publishEvent(
            HolidayNotificationEventFactory.holidayUpdated(updated.id, updated.holidayDate, updated.name)
        )
        return updated
    }

    fun delete(id: Long) {
        holidayService.delete(id)
        applicationEventPublisher.publishEvent(
            HolidayNotificationEventFactory.holidayDeleted(id)
        )
    }
}
