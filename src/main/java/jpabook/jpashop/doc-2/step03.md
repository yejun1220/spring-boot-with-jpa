# API 개발 고급 - 지연 로딩과 조회 성능 최적화

## log
### jpa.properties.hibernate:
- 콘솔창에 sout로 출력된다. `Hibernate: select~`

### logging.level.org.hibernate.SQL:
- 로그 형태로 남아 따로 저장할 수 있다. `DEBUG 10872 --- [nio-8080-exec-1] org.hibernate.SQL: select~`

## DTO 사용 주의점
- query 문에서 DTO를 사용하여 조회한다면 DTO 전용 리포지토리(dto query repository 등)를 만드는게 좋다.
- 기존 리포지토리에 DTO 조회문을 넣으면 그자체가 API 스펙이 될 수 있다.
- 유지 보수에 좋다.
- 각 리포지토리의 용도를 명확히 하자.
- 페치 조인은 DTO에서 사용 못한다.(순수 join만 가능) 따라서 페치 조인이 불가능하면 전용 리포지토리를 만드는 것이 좋다.
- DTO를 조회 후 필요한 값 DTO에 넣을 것이냐 vs 엔티티로 조회 후 특정 값만 DTO에 넣어줄 것이냐

## 쿼리 방식 선택 권장 순서
1. 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하면 페치 조인으로 성능을 최적화 한다.
3. 그래도 해결이 안되면 DTO로 직접 조회하는 방법을 사용한다.