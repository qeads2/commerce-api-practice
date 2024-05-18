

### 구현 범위
- 브랜드 및 아이템 등록 / 수정 / 삭제 API
  - PATCH /api/brands
    - CommerceController.kt:40
- 카테고리 별 최저가 브랜드, 가격, 총 금액 조회 API
  - GET /api/items/lowest-price
    - CommerceController.kt:20
- 최저가 브랜드 코디 세트 조회 API
  - GET /api/brands/lowest-price
    - CommerceController.kt:26
- 카테고리로 최저가, 최고가 브랜드 및 아이템 조회 API
  - GET /api/brands/price
    - CommerceController.kt:32

### 실행 방법
##### 빌드
```bash
./gradlew build
```

##### 테스트
```bash
./gradlew clean test
```

##### 실행
```bash
./gradlew build
./gradlew bootRun
```

### 추가 정보
##### 프로젝트 구조
commerce-api-practice
- api
  - 실행가능한 어플리케이션이 존재하는 모듈입니다.
    - api.controller 패키지에 컨트롤러가 존재합니다.
    - api.dto 패키지에 API 요청 및 응답을 위한 DTO가 존재합니다.
    - api.facade 패키지에 컨트롤러에서 사용하는 서비스를 래핑하는 Facade가 존재합니다. 알림 등의 기능이 추가될 경우 Facade에 추가하면 됩니다. 
- domain
  - 도메인 모델과 도메인 서비스가 존재하는 모듈입니다.
    - domain.info 패키지에 브랜드, 아이템, 카테고리 등의 도메인 모델이 존재합니다.
    - BrandStore, ItemStore 는 서비스가 저장소에 대한 의존성을 줄이기 위해 사용하는 인터페이스입니다.
- infra
  - 도메인의 저장소 구현체가 존재하는 모듈입니다. spring-data-jpa를 사용했으며, 데이터베이스는 H2를 사용했습니다.
  - itemRepository 의 경우 JPARepository, ItemQueryDslRepository, ItemJdbcTemplateRepository로 확장하여 구현하였습니다.

##### 기타
- 과제 요구사항에 따라 4개의 API 로 구현하였습니다.
- 테스트 코드는 각 CommerceController 부터 JPA 까지의 통합 테스트와 CommerceService 에 대한 유닛 테스트를 작성하였습니다.
  - 통합 테스트에서는 mockMvc 를 사용하여 API 테스트를 진행하였습니다.
  - 통합 테스트와 유닛 테스트에서는 객체 mocking 을 위해 Mockito를 사용했습니다.  
  