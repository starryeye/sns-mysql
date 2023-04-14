# 맴버 테이블 생성
create table Member
(
    id int auto_increment,
    email varchar(20) not null,
    nickname varchar(20) not null,
    birthday date not null,
    createdAt datetime not null,
    constraint member_id_uindex
        primary key (id)
);

# 맴버 데이터 조회
SELECT *
FROM Member;

# 맴버 데이터 조회 (id = 2)
SELECT *
FROM Member
where id = 2;

# 맴버 닉네임 변경 이력 테이블 생성
create table MemberNicknameHistory
(
    id int auto_increment,
    memberId int not null,
    nickname varchar(20) not null,
    createdAt datetime not null,
    constraint memberNicknameHistory_id_uindex
        primary key (id)
);

# 팔로우 테이블 생성
create table Follow
(
    id int auto_increment,
    fromMemberId int not null,
    toMemberId int not null,
    createdAt datetime not null,
    constraint Follow_id_uindex
        primary key (id)
);

# fromMemberId 와 toMemberId 를 조합하여 유니크한 인덱스 생성, MySQL에서의 유니크 제약조건을 위함
create unique index Follow_fromMemberId_toMemberId_uindex
    on Follow (fromMemberId, toMemberId);

# 팔로우 데이터 조회
SELECT *
FROM Follow;

# 게시글 테이블 생성
create table POST
(
    id int auto_increment,
    memberId int not null,
    contents varchar(100) not null,
    createdDate date not null,
    createdAt datetime not null,
    constraint POST_id_uindex
        primary key (id)
);

# 게시글 id(pk) 갯수 조회
SELECT count(id)
FROM POST;

explain SELECT memberId, createdDate, count(id) as count
        FROM POST
        WHERE memberId = 4 and createdDate between '1970-01-01' and '2023-12-31'
        GROUP BY memberId, createdDate;#


#
explain SELECT count(id)
        FROM POST
        WHERE memberId = 4 and createdDate between '1970-01-01' and '2023-12-31';

# 게시글 테이블 인덱스 생성
create index POST__index_member_id
    on POST (memberId);
# 게시글 테이블 인덱스 생성
create index POST__index_created_date
    on POST (createdDate);
# 게시글 테이블 인덱스 생성
create index POST__index_member_id_created_date
    on POST (memberId, createdDate);

# 데이터 분포 확인
SELECT memberId, count(id)
FROM POST
GROUP BY memberId;
# 데이터 분포 확인
SELECT createdDate, count(id)
FROM POST
GROUP BY createdDate
order by 2 desc;
# 데이터 분포 확인
SELECT count(distinct (createdDate))
FROM POST;

# memberId 의 인덱스로 조회
explain SELECT memberId, createdDate, count(id) as count
        FROM POST USE INDEX (POST__index_member_id)
        WHERE memberId = 4 AND createdDate BETWEEN '1970-01-01' AND '2023-12-31'
        GROUP BY memberId, createdDate;
# createdDate 의 인덱스로 조회
explain SELECT memberId, createdDate, count(id) as count
        FROM POST use index (POST__index_created_date)
        WHERE memberId = 4 and createdDate between '1970-01-01' and '2023-12-31'
        GROUP BY memberId, createdDate;
# memberId, createdDate 의 인덱스로 조회
explain SELECT memberId, createdDate, count(id) as count
        FROM POST use index (POST__index_member_id_created_date)
        WHERE memberId = 4 and createdDate between '1970-01-01' and '2023-12-31'
        GROUP BY memberId, createdDate;

# 페이지네이션
SELECT *
FROM POST
WHERE memberId = 3
ORDER BY id DESC #MySQL 에서는 해당 정렬기준을 주지않으면 pk 기준 오름 차순 으로 정렬
LIMIT 2 # 2개만 조회
OFFSET 0; # 0번째부터 조회


# timeline table 생성
create table Timeline
(
    id int auto_increment,
    memberId int not null,
    postId int not null,
    createdAt datetime not null,
    constraint Timeline_id_uindex
        primary key (id)
);

select *
from Timeline;

select *
from follow;



#인덱스가 없는 조건으로 lock read 시 불필요한 데이터들이 잠길 수 있다.
select *
from post
where memberId = 1;


# select for update 쿼리는 트랜잭션을 사용해야한다.(락을 걸어줘야 하기 때문)
  start transaction;
select *
from post
where memberId = 1 and contents = 'string'
    for update;
commit;

# 트랜잭션 확인
select * from information_schema.INNODB_TRX;
# 락 확인
select * from performance_schema.data_locks;


# post table 에 likeCount 컬럼 추가
# 토이 프로젝트라면 마지막에 default 0 을 추가해도 된다.
alter table post add column likeCount int;
# post table 에 version 컬럼 추가
# alter table post add column version int;
alter table post drop column version;
# default 0 을 추가해야 한다. 이유는 Post 엔티티 주석 참고
alter table post add column version int default 0;

# PostLike 테이블 생성
create table PostLike (
                          id int auto_increment,
                          memberId int not null,
                          postId int not null,
                          createdAt datetime not null,
                          constraint PostLike_id_uindex
                              primary key (id)
)

    # PostLike 조회
select *
from PostLike;