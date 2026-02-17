package io.glory.infrastructure.notification

import io.glory.common.notification.NotificationEvent
import io.glory.common.notification.NotificationPort
import io.glory.infrastructure.slack.SlackNotificationService
import org.springframework.stereotype.Component

@Component
class SlackNotificationAdapter(
    private val slackNotificationService: SlackNotificationService,
) : NotificationPort {

    override fun supports(event: NotificationEvent): Boolean =
        event is NotificationEvent.Slack

    override fun send(event: NotificationEvent) {
        if (event !is NotificationEvent.Slack) return
        slackNotificationService.notify(message = event.message)
    }
}

