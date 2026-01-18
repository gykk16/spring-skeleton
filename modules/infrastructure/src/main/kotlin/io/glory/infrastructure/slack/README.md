# Slack Notification

Slack 알림 전송을 위한 모듈입니다. Kotlin DSL을 사용하여 직관적으로 메시지를 작성하고, Block Kit과 색상 바(Attachment)를 지원합니다.

## 설정

### application.yml

```yaml
slack:
  enabled: true
  bot-token: ${SLACK_BOT_TOKEN}
  default-channel: "#general"
  retry:
    max-attempts: 3
    backoff-ms: 1000
    max-backoff-ms: 10000
```

### Slack Bot Token 발급

1. [Slack API](https://api.slack.com/apps) 접속
2. **Create New App** → **From scratch**
3. **OAuth & Permissions** 메뉴에서 Bot Token Scopes 추가:
   - `chat:write` (메시지 전송)
   - `chat:write.public` (public 채널에 초대 없이 전송)
4. **Install to Workspace**
5. **Bot User OAuth Token** 복사 (`xoxb-...`)

## 사용법

### 1. 간단한 메시지 전송

```kotlin
slackNotificationService.notify("#general", slackMessage {
    text("Hello, World!")
})
```

### 2. Block Kit 메시지

```kotlin
slackNotificationService.notify("#deployments", slackMessage {
    header("🚀 배포 완료")

    section {
        markdown("*애플리케이션:* `user-service`\n*버전:* `v2.3.1`")
    }

    divider()

    section {
        fields(
            "*환경*", "Production",
            "*소요시간*", "3분 24초"
        )
    }

    context {
        markdown("배포자: *@john.doe*")
    }

    actions {
        button("로그 보기", ButtonStyle.PRIMARY) {
            url("https://logs.example.com")
        }
        button("롤백", ButtonStyle.DANGER) {
            actionId("rollback")
        }
    }
})
```

### 3. 색상 바 사용 (Attachment)

Block Kit은 색상 바를 지원하지 않으므로, Attachment 내에 Block을 넣어서 사용합니다.

```kotlin
// 성공 (녹색)
slackNotificationService.notify("#alerts", slackMessage {
    attachment(SlackColor.SUCCESS) {
        section { markdown("✅ *배포 완료*") }
    }
})

// 경고 (노란색)
slackNotificationService.notify("#alerts", slackMessage {
    attachment(SlackColor.WARNING) {
        section { markdown("⚠️ *디스크 사용량 85%*") }
    }
})

// 에러 (빨간색)
slackNotificationService.notify("#errors", slackMessage {
    attachment(SlackColor.DANGER) {
        section { markdown("🚨 *결제 처리 실패*") }
    }
})

// 커스텀 색상
slackNotificationService.notify("#general", slackMessage {
    attachment("#7B68EE") {  // 보라색
        section { markdown("💜 *커스텀 색상*") }
    }
})
```

### 4. 편의 메서드

```kotlin
// 성공 알림
slackNotificationService.notifySuccess("#alerts", "배포 완료", "v1.2.3 배포됨")

// 에러 알림 (스택트레이스 포함)
slackNotificationService.notifyError("#errors", "에러 발생", exception)

// 경고 알림
slackNotificationService.notifyWarning("#alerts", "경고", "디스크 사용량 85%")

// 정보 알림
slackNotificationService.notifyInfo("#info", "공지", "시스템 점검 예정")
```

### 5. 스레드 답장

```kotlin
// 1. 원본 메시지 전송
val response = slackNotificationService.notify("#deployments", slackMessage {
    header("🚀 배포 시작")
    section { markdown("*애플리케이션:* `user-service`") }
})

// 2. 스레드에 답장
slackNotificationService.reply("#deployments", response.ts, slackMessage {
    text("✅ 빌드 완료 (1/3)")
})

slackNotificationService.reply("#deployments", response.ts, slackMessage {
    text("✅ 테스트 통과 (2/3)")
})

slackNotificationService.reply("#deployments", response.ts, slackMessage {
    text("✅ 배포 완료 (3/3)")
})
```

## DSL 레퍼런스

### SlackMessageBuilder

| 메서드 | 설명 |
|--------|------|
| `channel(channel)` | 채널 설정 |
| `text(text)` | 기본 텍스트 (알림 미리보기용) |
| `threadTs(ts)` | 스레드 답장 |
| `header(text)` | Header 블록 |
| `section { }` | Section 블록 |
| `divider()` | Divider 블록 |
| `actions { }` | Actions 블록 |
| `context { }` | Context 블록 |
| `attachment(color) { }` | Attachment (색상 바) |

### SectionBlockBuilder

| 메서드 | 설명 |
|--------|------|
| `text(text)` | Plain text |
| `markdown(text)` | Markdown text |
| `fields(vararg texts)` | 2열 필드 (최대 10개) |
| `accessory { }` | 액세서리 (버튼, 이미지) |

### ActionsBlockBuilder

| 메서드 | 설명 |
|--------|------|
| `button(text, style) { }` | 버튼 추가 |

### ContextBlockBuilder

| 메서드 | 설명 |
|--------|------|
| `text(text)` | Plain text |
| `markdown(text)` | Markdown text |
| `image(url, altText)` | 이미지 |

### AttachmentBuilder

Attachment 내부에서 Block Kit 사용:

| 메서드 | 설명 |
|--------|------|
| `header(text)` | Header 블록 |
| `section { }` | Section 블록 |
| `divider()` | Divider 블록 |
| `actions { }` | Actions 블록 |
| `context { }` | Context 블록 |

### SlackColor

| 색상 | 값 | 용도 |
|------|-----|------|
| `DEFAULT` | `#dddddd` | 기본 (회색) |
| `SUCCESS` | `#36a64f` | 성공 (녹색) |
| `WARNING` | `#ffcc00` | 경고 (노란색) |
| `DANGER` | `#ff0000` | 에러 (빨간색) |
| `INFO` | `#439FE0` | 정보 (파란색) |

### ButtonStyle

| 스타일 | 설명 |
|--------|------|
| `DEFAULT` | 기본 (회색) |
| `PRIMARY` | 강조 (녹색) |
| `DANGER` | 위험 (빨간색) |

## 비활성화

테스트 환경이나 로컬에서 Slack 전송을 비활성화하려면:

```yaml
slack:
  enabled: false
```

비활성화 시 메시지는 로그로만 출력되고 실제 전송되지 않습니다.

## 에러 처리

| Slack Error | 처리 |
|-------------|------|
| `channel_not_found` | SlackException (채널 확인 필요) |
| `not_in_channel` | SlackException (봇 초대 필요) |
| `invalid_auth` | SlackException (토큰 확인 필요) |
| `rate_limited` | 자동 재시도 (exponential backoff) |

## 파일 구조

```
slack/
├── SlackClient.kt              # SDK 래퍼
├── SlackClientConfig.kt        # Bean 설정
├── SlackProperties.kt          # 설정 프로퍼티
├── SlackException.kt           # 커스텀 예외
├── SlackResponse.kt            # 응답 모델
├── SlackNotificationService.kt # 알림 서비스
├── message/
│   ├── SlackMessage.kt         # 메시지 도메인 모델
│   ├── SlackMessageBuilder.kt  # DSL 빌더
│   ├── SlackDsl.kt             # DSL 마커
│   ├── SlackColor.kt           # 색상 enum
│   ├── ButtonStyle.kt          # 버튼 스타일 enum
│   ├── SectionBlockBuilder.kt  # Section 블록 빌더
│   ├── ActionsBlockBuilder.kt  # Actions 블록 빌더
│   ├── ContextBlockBuilder.kt  # Context 블록 빌더
│   └── AttachmentBuilder.kt    # Attachment 빌더
└── README.md
```
