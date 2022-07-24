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


