drop table member_mrole_set;
delete from member where member_id = 'leehwoo0119@naver.com';

SHOW DATABASES;
USE board_admin_image;
drop table member;

SHOW CREATE TABLE board_admin_image;
SELECT
    CONSTRAINT_NAME, TABLE_NAME
FROM
    information_schema.KEY_COLUMN_USAGE
WHERE
    TABLE_NAME = 'board_admin_image'
  AND CONSTRAINT_SCHEMA = DATABASE();

delete from board_admin_images where id = '2';

SET foreign_key_checks = 1;
drop table board_admin;
drop table board_admin_image;

ALTER TABLE board_admin_image DROP FOREIGN KEY FKbqxbqq8gqurk1pk8m768x6d0l;
ALTER TABLE board_admin_image DROP FOREIGN KEY FKom80o501rciwk8fiad6xlm2r6;
ALTER TABLE board_admin_image DROP FOREIGN KEY FKbqxbqq8gqurk1pk8m768x6d0l;

select member_id
from member
where member_name = 'lee' and member_phone_number = '01012345678';

UPDATE member SET member_password= '1111' where member_id = 'lee';

create table persistent_logins(
    username varchar(64) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null);

INSERT INTO member (
    member_id,
    member_name,
    member_phone_number,
    member_email,
    member_password,
    member_postcode,
    member_address,
    member_detail_address,
    member_mileage,
    member_is_activate,
    member_is_social_activate
) VALUES (
             'adminaccount',
             '관리자',
             '01012345678',
             'adminaccount@test.com',
             '1111',
             '12345',
             '서울시 강남구',
             '테헤란로 123',
             0,
             true,
             false
         );

INSERT INTO member_role (member_id, role)
VALUES ('adminaccount', 'ADMIN');

DESC member_mrole_set;

DROP TABLE IF EXISTS member_mrole_set;

CREATE TABLE member_mrole_set (
                                  member_id VARCHAR(255) NOT NULL,
                                  role ENUM('USER','ADMIN') NOT NULL,
                                  PRIMARY KEY(member_id, role),
                                  CONSTRAINT fk_member FOREIGN KEY(member_id) REFERENCES member(member_id)
);

SELECT * FROM member_role;

DESC member_role;