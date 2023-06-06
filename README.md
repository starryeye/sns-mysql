# sns-mysql

## Branch  
- main : Spring Data Jpa/Redis + JdbcTemplate version  
- jdbctemplate : JdbcTemplate version   
  
## Topic
- 대용량 트래픽 처리에 필요한 고려 사항  
  -  정규화, 비정규화
  -  조회 최적화를 위한 인덱스
  -  오프셋/커서 기반 페이지네이션
  -  Fan out on read, Fan out on write
  -  Transaction
  -  Optimistic/Pessimistic Lock

## API 및 DB
설명은 아래 jdbctemplate branch URL 참조  
https://github.com/starryeye/sns-mysql/tree/release/snapshot-jdbctemplate

## Test
- Bulk insert test
- Jdbc Template -> Spring Data Jpa 전환에 따른 로직 정합성 Test
  
## Dependency
- Java 17
- Spring Boot 3.0.5
- Spring Web
- Spring Data Jpa
- Spring Data Redis
- Lombok
- MySQL
- Springdoc
- Easy Random

## Tool
- Intellij Ultimate
- Docker
- DataGrip
