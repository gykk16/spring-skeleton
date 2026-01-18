package io.glory.infrastructure.slack.message

/**
 * Slack 버튼 스타일
 */
enum class ButtonStyle(val value: String?) {
    DEFAULT(null),
    PRIMARY("primary"),
    DANGER("danger"),
}
