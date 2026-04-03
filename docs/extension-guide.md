# Extension Guide

## 새 sender 추가

1. `core/src/main/java/com/notification/core/senders`에 sender 구현을 추가한다.
2. `ChannelId`를 고정한다.
3. `NotificationSender`를 구현한다.
4. `config`에서 빈으로 등록한다.

## 주의점

- sender는 네트워크 실패를 직접 삼키지 말고 `NotificationResult`로 돌려준다.
- 콘솔 출력은 로컬 확인용으로만 사용한다.
- 외부 API 호출은 타임아웃을 명시한다.
- SMTP sender는 recipients를 모두 반영해야 한다.
- Slack sender는 webhook payload를 단순하고 안정적으로 유지한다.
