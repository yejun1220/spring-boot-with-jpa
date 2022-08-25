# API 개발 고급 - 컬렉션 조회 최적화 메모 사항들

## DTO에 @getter 쓰는 이유
- 소스 코드에서는 DTO 필드에 접근하지 않지만 DTO를 반환해준 후 springboot가 내부적으로 필드값을 사용하기 때문에 getter가 필요하다.(json 형태로 만들기 위해서 등)

## LAZY Loading에 따른 호출
- LAZY 로딩은 프록시 객체를(`ByteBuddyInterceptor`) 반환해주기 때문에 연관관계 조회시 타입이 맞지 않아 에러가 난다.
- 프록시 객체를 null 값으로 대체해주는 것이 `hibernate5Module` 이다.
- `hibernate5Module` 선언 후, 실제 데이터를 가져오고 싶으면 실제 객체에 접근하거나, hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);를 통해 가져온다.
### 바로 실제 객체에 접근하면 안되나?
- 실제 객체에 접근해도 프록시 객체가 없어지는 것이 아닌 링크를 해주기 때문에 의미가 없다.

## EAGAR 작동 방식
OneToMany EAGAR (반대편 LAZY, 원래에 다른 EAGAR 없음)  
-> 전체조회 쿼리 1회 + Many 수(2)만큼 조회 / 조인 X

OneToMany EAGAR (반대편 LAZY, 원래에 다른 EAGAR 있음, 다른 EAGAR 안에 또 EAGAR가 있음)  
-> 전체조회 쿼리 1회 + Many 수(2)만큼 조회 + / 조인 O  
뒤 One과 뒤 One에 속한 앞 One을 조인한, 뒤 One의 갯수 만큼 조회 +  
앞 One과 앞 One에 속한 뒤 One을 조인한, 뒤 One의 갯수 만큼 조회


OneToMany EAGAR (반대편 EAGAR)  
-> 전체조회 쿼리 1회 + Many 수(2)만큼 조회 / 조인 X

ManyToOne EAGAR(반대편 LAZY)  
-> 전체조회 쿼리 1회 + Many(3)에 속하는 One(2) 만큼 조회 / 조인 O  
Many에 EAGAR인 것을 가져오기 위해 그만큼 가져오고 가져온 것에 EAGAR가 있므로 조인해서 가져온다.  
Many에 EAGAR인 것을 가져오기 위해 그만큼 가져오고 가져온 것이 LAZY이므로 조인을 안한다.  

ManyToOne EAGAR(반대편 EAGAR)  
-> 전체조회 쿼리 1회 + Many(3)에 속하는 One(2) 만큼 조회  / 조인 O  

OneToOne EAGAR(반대편 LAZY)  
-> 전체조회 쿼리 1회 +  
앞 One에 속한 뒤 One의 갯수 만큼 조회  
뒤 One과 뒤 One의 앞 One을 조인한 뒤 One의 갯수 만큼 조회  

OneToOne EAGAR(반대편 EAGAR)  
-> 전체조회 쿼리 1회 +  
앞 One과 앞 One에 속한 뒤 One을 조인한, 뒤 One의 갯수 만큼 조회 +  
뒤 One과 뒤 One에 속한 앞 One을 조인한, 뒤 One의 갯수 만큼 조회

------------------------------------------------
처음 조회 시(OneToOne 제외)
1. LAZY면 1번만 쿼리나감
2. EAGAR면 EAGAR가 있는 엔티티의 갯수 만큼 쿼리나감
+ 반대편으로 갔을 때 EAGAR가 있으면 EAGAR와 반대편을 조인해서 가져 옴

처음 조회 시(OneToOne)
1. LAZY면 1번만 쿼리나감
2. EAGAR면 EAGAR가 있는 엔티티의 갯수 만큼 쿼리가 나가고 반대편에서도 똑같이 진행


## Distinct
- 컬렉션 조회 시(One to Many), sql에서 조인으로 인해 Many의 갯수만큼 row가 늘어난다.
- 데이터가 늘어나므로 페이징이 불가능하고 원하는 데이터를 얻지 못 할 수 있다.
- distinct를 통해 중복을 제거할 수 있다.(sql의 distinct와 다르다.)
- sql: 2개의 order가 각각 2개의 orderItem을 가지고 있다면 조인시 4개가 출력
- jpa: 같은 order에 대하여 여러개의 sql 객체가 나와도 같은 jpa 객체를 보장해준다. -> 같은 주소의 객체를 distinct로 제거
- jpa: sql통해 db에서 데이터와 갯수만 가져올 뿐, 객체는 같다.(페이징 불가능)
- 페이징 시도시 warning 로그와 함께 sql문에는 페이징 쿼리가 존재하지 않는 것을 알 수 있다.
- 메모리에서 페이징 처리를 한다.(데이터가 많을 경우 out of memory 발생)
- 컬렉션 페치 조인은 1개만 사용해야 한다. 한 모델에 2개의 컬렉션이든, 컬렉션 안에 컬렉션 형태든

## 한계 돌파
- hibernate.default_batch_fetch_size: 글로벌 설정
- fetchsize: 개별 설정