# Delivery Model

`DefaultNotificationService`는 `SenderRegistry`에서 `ChannelId`에 맞는 sender를 찾아 실행한다.

## 흐름

1. 호출자가 `SendNotificationCommand` 또는 도메인 요청을 만든다.
2. `config`가 `SenderRegistry`와 `NotificationService`를 제공한다.
3. `core`가 적절한 sender를 찾아 메시지를 전달한다.
4. sender는 `NotificationResult`로 성공/실패를 반환한다.

## 현재 sender

- `console`: stdout 출력
- `webhook`: HTTP POST
- `email`: SMTP
- `slack`: Slack webhook

## 제외 범위

- 재시도 큐
- DLQ
- 템플릿 엔진
- 사용자별 라우팅 정책
