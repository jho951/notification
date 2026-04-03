# Channel Adapters

`notification`은 sender를 채널별 어댑터로 본다.

## 포함

- `console`
- `webhook`
- `email`
- `slack`

## 원칙

- 어댑터는 메시지 계약을 변경하지 않는다.
- 어댑터는 전송 방식만 책임진다.
- 인증과 정책은 상위 계층 책임이다.

## 확장 기준

- 외부 벤더용 webhook adapter

## 제외

- 큐
- 저장소
- 스케줄링
