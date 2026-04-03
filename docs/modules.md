# Modules

## 역할

- `api`: 외부 호출용 command/response 계약
- `core`: notification service와 sender 구현
- `config`: Spring Boot auto-configuration
- `starter`: `config`를 가져오는 진입점

## 현재 sender

- `console`: 로컬 확인
- `webhook`: 외부 시스템 연동
- `email`: SMTP 이메일 전송
- `slack`: Slack webhook 전송

## 배포 기준

- `api`, `core`, `config`, `starter`는 배포 대상
- 테스트 픽스처는 별도 모듈로 분리할 때만 배포한다
