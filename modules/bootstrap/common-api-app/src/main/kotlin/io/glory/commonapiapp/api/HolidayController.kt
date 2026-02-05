package io.glory.commonapiapp.api

import io.glory.commonapiapp.api.dto.BulkCreateHolidayApiRequest
import io.glory.commonapiapp.api.dto.CreateHolidayApiRequest
import io.glory.commonapiapp.api.dto.HolidayDto
import io.glory.commonapiapp.api.dto.HolidaysResponse
import io.glory.commonapiapp.api.dto.UpdateHolidayApiRequest
import io.glory.commonapplication.service.HolidayService
import io.glory.commonweb.response.resource.ApiResource
import io.glory.domain.holiday.CreateHolidayRequest
import io.glory.domain.holiday.UpdateHolidayRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/holidays")
class HolidayController(
    private val holidayService: HolidayService,
) {

    @GetMapping("/{year}")
    fun getByYear(@PathVariable year: Int): ResponseEntity<ApiResource<HolidaysResponse>> {
        val holidays = holidayService.findByYear(year)
        return ApiResource.success(HolidaysResponse.from(holidays))
    }

    @GetMapping("/{year}/{month}")
    fun getByYearAndMonth(
        @PathVariable year: Int,
        @PathVariable month: Int,
    ): ResponseEntity<ApiResource<HolidaysResponse>> {
        val holidays = holidayService.findByYearAndMonth(year, month)
        return ApiResource.success(HolidaysResponse.from(holidays))
    }

    @GetMapping("/{year}/{month}/{day}")
    fun getByDate(
        @PathVariable year: Int,
        @PathVariable month: Int,
        @PathVariable day: Int,
    ): ResponseEntity<ApiResource<HolidaysResponse>> {
        val holidays = holidayService.findByDate(year, month, day)
        return ApiResource.success(HolidaysResponse.from(holidays))
    }

    @PostMapping
    fun create(@RequestBody request: CreateHolidayApiRequest): ResponseEntity<ApiResource<HolidayDto>> {
        val holiday = holidayService.create(
            CreateHolidayRequest(
                holidayDate = request.holidayDate,
                name = request.name,
            )
        )
        return ApiResource.success(HolidayDto.from(holiday))
    }

    @PostMapping("/bulk")
    fun createBulk(@RequestBody request: BulkCreateHolidayApiRequest): ResponseEntity<ApiResource<List<HolidayDto>>> {
        val requests = request.holidays.map {
            CreateHolidayRequest(
                holidayDate = it.holidayDate,
                name = it.name,
            )
        }
        val holidays = holidayService.createAll(requests)
        return ApiResource.success(holidays.map { HolidayDto.from(it) })
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateHolidayApiRequest,
    ): ResponseEntity<ApiResource<HolidayDto>> {
        val holiday = holidayService.update(
            id,
            UpdateHolidayRequest(
                holidayDate = request.holidayDate,
                name = request.name,
            )
        )
        return ApiResource.success(HolidayDto.from(holiday))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResource<String>> {
        holidayService.delete(id)
        return ApiResource.success()
    }
}
