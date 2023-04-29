# sns-mysql

## Api

hello
- 서버 동작 확인  
  - GET http://localhost:8080/hello  
  
member  
- 회원 정보 조회  
  - GET http://localhost:8080/members/{id}  
  
- 회원 닉네임 변경  
  - PUT http://localhost:8080/members/{id}  
  
- 회원 등록  
  - POST http://localhost:8080/members  
  
- 회원 닉네임 변경 이력 조회  
  - GET http://localhost:8080/members/{memberId}/nickname-histories  
  
post
- 게시물 등록  
  - POST http://localhost:8080/posts  
  
- 기간별 회원의 게시물 등록 수 조회  
  - GET http://localhost:8080/posts/posts/daily-post-counts  
  
- 회원의 게시물 조회  
  - GET http://localhost:8080/posts/members/{memberId}  
  
- 회원의 게시물 조회  
  - GET http://localhost:8080/posts/members/{memberId}/by-cursor  
  
- 회원이 팔로우한 회원의 게시물 조회  
  - GET http://localhost:8080/posts/members/{memberId}/timeline  
  
- 게시물 좋아요 기능  
  - POST http://localhost:8080/posts/{postId}/like/v1  
  
- 게시물 좋아요 기능  
  - POST http://localhost:8080/posts/{postId}/like/v2  
  
follow
- 팔로우 기능  
  - POST http://localhost:8080/follows/{fromMemberId}/{toMemberId}  
  
- 회원이 팔로우한 회원 조회  
  - GET http://localhost:8080/follows/{fromMemberId}  
  
## Implement & Study Point
- Offset Pagination vs Cusor Pagination  
- 정규화, 비정규화  
- non clustered index, clusted index  
- covering index  
- fan out on write(push model), fan out on read(pull model), CAP 이론  
- transaction
- read lock, write lock
- pessimistic lock, optimistic lock
  
## Architecture
- Layered Architecture
  
## DB
<img width="404" alt="image" src="https://user-images.githubusercontent.com/33487061/232205101-617c2565-a47a-4af6-8738-aeca73fc5ede.png">
  
## Dependency
- Java 17
- Spring Framework
- Spring Boot 3.0.5
- Spring Web
- Jdbc api
- Lombok
- MySQL
- Springdoc
  
## Tool
- Intellij Ultimate
- Docker
- DataGrip
