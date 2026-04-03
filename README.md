# notification

`notification`은 메시지 발송을 위한 작은 OSS 모듈입니다.

## 구성

- `api`: 외부 호출용 계약
- `core`: 공통 도메인, 서비스, sender 구현
- `config`: Spring Boot auto-configuration
- `starter`: Spring Boot 진입점

## sender

- `console`
- `webhook`
- `email`
- `slack`

## 빌드

```bash
./gradlew test
```

## 배포

- tag `v*` 푸시 시 GitHub Actions가 테스트와 Maven Central 배포를 수행합니다.

## 문서

1. [docs/README.md](docs/README.md)
2. [docs/architecture.md](docs/architecture.md)
3. [docs/modules.md](docs/modules.md)
4. [docs/delivery-model.md](docs/delivery-model.md)
5. [docs/channel-adapters.md](docs/channel-adapters.md)
6. [docs/api-model.md](docs/api-model.md)
