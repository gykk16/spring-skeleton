# Project Convention

## API Response

> **IMPORTANT**: All APIs except `GlobalController` and `HomeController` must respond with `ApiResource` format.

### Correct Examples

```kotlin
@GetMapping("/{id}")
fun getUser(@PathVariable id: Long): ResponseEntity<ApiResource<UserDto>> =
    ApiResource.success(userService.findById(id))

@PostMapping
fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<ApiResource<UserDto>> =
    ApiResource.success(userService.create(request))

@DeleteMapping("/{id}")
fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResource<String>> {
    userService.delete(id)
    return ApiResource.success()
}

@GetMapping
fun getUsers(pageable: Pageable): ResponseEntity<ApiResource<List<UserDto>>> =
    ApiResource.ofPage(userService.findAll(pageable))
```

### Incorrect Examples

```kotlin
// Bad: Not using ApiResource
@GetMapping("/{id}")
fun getUser(@PathVariable id: Long): UserDto = userService.findById(id)

// Bad: Using only ResponseEntity
@GetMapping("/{id}")
fun getUser(@PathVariable id: Long): ResponseEntity<UserDto> =
    ResponseEntity.ok(userService.findById(id))
```

## Date and Time Format

> **IMPORTANT**: Use ISO-8601 format for all date and time representations.

### Standard Formats

| Type | Format | Example |
|------|--------|---------|
| Date | `yyyy-MM-dd` | `2025-01-02` |
| Time | `HH:mm:ss` or `HH:mm:ss.SSS` | `14:30:00` or `14:30:00.123` |
| DateTime | `yyyy-MM-dd'T'HH:mm:ss` or `yyyy-MM-dd'T'HH:mm:ss.SSS` | `2025-01-02T14:30:00` or `2025-01-02T14:30:00.123` |
| DateTime with timezone (ZonedDateTime) | `yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'` or `yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'` | `2025-01-02T14:30:00+09:00[Asia/Seoul]` or `2025-01-02T14:30:00.123+09:00[Asia/Seoul]` |
| DateTime UTC | `yyyy-MM-dd'T'HH:mm:ss'Z'` or `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` | `2025-01-02T05:30:00Z` or `2025-01-02T05:30:00.123Z` |

> **NOTE**: `.SSS` (milliseconds) is optional. Include when precision is required.

### Correct Examples

```kotlin
// API Response DTO
data class EventDto(
    val id: Long,
    val name: String,
    val startDate: LocalDate,        // Serialized as: "2025-01-02"
    val startTime: LocalTime,        // Serialized as: "14:30:00"
    val createdAt: LocalDateTime,    // Serialized as: "2025-01-02T14:30:00"
    val scheduledAt: ZonedDateTime   // Serialized as: "2025-01-02T14:30:00+09:00[Asia/Seoul]"
)

// Request parameter
@GetMapping("/events")
fun getEvents(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
): ResponseEntity<ApiResource<List<EventDto>>>
```

### Incorrect Examples

```kotlin
// Bad: Not using ISO-8601 format
data class EventDto(
    val date: String  // "01/02/2025" or "2025/01/02"
)

// Bad: Non-standard custom format
@GetMapping("/events")
fun getEvents(
    @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") date: LocalDate  // "20250102"
): ResponseEntity<ApiResource<List<EventDto>>>
```

### JsonFormat Annotation Usage

Use `@JsonFormat` with appropriate use-site targets for JSON serialization/deserialization.

| Annotation Target | Use Case | Description |
|-------------------|----------|-------------|
| `@param:JsonFormat` | Request (Deserialization) | Applied to constructor parameters for parsing incoming JSON |
| `@get:JsonFormat` | Response (Serialization) | Applied to getter for formatting outgoing JSON |
| `@field:JsonFormat` | Both directions | Applied to field for both request and response |

#### Correct Examples

```kotlin
// Response only - use @get:JsonFormat
data class EventResponse(
    val id: Long,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    val scheduledAt: ZonedDateTime
)

// Request only - use @param:JsonFormat
data class CreateEventRequest(
    val name: String,
    @param:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startAt: LocalDateTime,
    @param:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    val scheduledAt: ZonedDateTime
)

// Both directions - use @field:JsonFormat
data class EventDto(
    val id: Long,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    val scheduledAt: ZonedDateTime
)
```

#### Incorrect Examples

```kotlin
// Bad: Using @param for response serialization (won't work)
data class EventResponse(
    @param:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime  // Output format not applied
)

// Bad: Using @get for request deserialization (won't work)
data class CreateEventRequest(
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startAt: LocalDateTime  // Input parsing not applied
)
```
