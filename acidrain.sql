DROP DATABASE IF EXISTS acidrain;
CREATE DATABASE IF NOT EXISTS acidrain;

use acidrain;

show tables;


-- -------------------------------------------------------------
-- --		wordtype table
-- --		pri key: typeidx / to be ref: typename
-- -------------------------------------------------------------
DELETE TABLE IF EXISTS wordtype;
CREATE TABLE IF NOT EXISTS `wordtype` (
	`typeidx` integer(3) NOT NULL,
	`typename` varchar(15) NOT NULL,
	`typeflag` integer(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (`typeidx`)
);

DESC wordtype;

INSERT INTO
	wordtype (typeidx, typename)
VALUES
	(1, 'IT'),
	((SELECT num FROM (SELECT MAX(typeidx) + 1 num FROM wordtype) numF ), 'food'),
	((SELECT num FROM (SELECT MAX(typeidx) + 1 num FROM wordtype) numF), 'general'),
	((SELECT num FROM (SELECT MAX(typeidx) + 1 num FROM wordtype) numF), 'game')
;

SELECT * FROM wordtype;

-- ``````````````````````````````````````````````````````````````


-- --------------------------------------------------------------
-- 	words table
-- 	pri key: wordidx / fri key: typeidx
-- --------------------------------------------------------------

DROP TABLE IF EXISTS words;
CREATE TABLE IF NOT EXISTS words(
	wordidx integer(4) NOT NULL AUTO_INCREMENT,
	word varchar(21) NOT NULL,
	typeidx integer(1) NOT NULL,
	wordflag integer(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (wordidx),
	CONSTRAINT type_idx_ref_wtype FOREIGN KEY (typeidx) 
		REFERENCES wordtype(typeidx)
);

DESC words;

INSERT INTO
	words (word, typeidx)
VALUES
	('컴퓨터', 1), ('자바', 1), ('프로그래밍', 1), ('기본값', 1),
	('안드로이드', 1), ('소켓', 1), ('서버소켓', 1), ('데이터', 1),
	('배열', 1), ('네트워크', 1), ('프로토콜', 1), ('상속', 1),
	('클래스', 1), ('다형성', 1), ('은닉', 1), ('객체지향', 1),
	('이클립스', 1), ('예약어', 1), ('알고리즘', 1), ('천궁', 1), 
	/*((SELECT num FROM (SELECT MAX(wordidx) + 1 num FROM wordidx) numT),
			'자바', 1)*/
	('젤리빈', 2), ('아이스크림', 2), ('게살버거', 2), ('칙촉', 2), 
	('리콜라', 2), ('사또밥', 2), ('달걀프라이', 2), ('스윙칩', 2), 
	('상하이디럭스', 2), ('갈릭스테이크', 2), ('짜장면', 2), ('삼겹살', 2), 
	('오겹살', 2), ('드림카카오', 2), ('탕수육은부먹', 2), ('피자', 2), 
	('리콜라', 2), ('팥빙수', 2), ('냉면', 2), ('찹쌀떡', 2), 
	
	('면역', 3), ('문화상품권', 3), ('휴지통', 3), ('집게', 3), 
	('신발장', 3), ('매트리스', 3), ('행주', 3), ('싱크대', 3), 
	('오스트레일리아', 3), ('비행기', 3), ('우주선', 3), ('나사', 3), 
	('눈물', 3), ('아마존', 3), ('화살', 3), ('핵융합', 3), 
	('책가방', 3), ('묘목', 3), ('별자리', 3), ('해양심층수', 3),
	
	('한조대기중', 4), ('시공의폭풍', 4), ('히오스', 4), ('시공좋아', 4), 
	('고급시계', 4), ('쓰랄', 4), ('말퓨리온', 4), ('블랙하트항만', 4), 
	('젠야타', 4), ('리신', 4), ('실바나스', 4), ('리치왕의분노', 4), 
	('라인하르트', 4), ('아나', 4), ('정크랫', 4), ('눔바니', 4), 
	('태양노래농장', 4), ('츄릅아삭당근', 4), ('노움', 4), ('용개', 4)
;

SELECT * FROM words;

SELECT CHAR_LENGTH(word) clength, COUNT(CHAR_LENGTH(word)) ccnt 
	FROM words GROUP BY clength;

-- ``````````````````````````````````````````````````````````````


-- --------------------------------------------------------------
-- 	users table
-- 	pri key: useridx / not null unique: username
-- --------------------------------------------------------------

DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
	useridx integer(1) AUTO_INCREMENT,	-- 귀차니즘이 잘못했네
	username varchar(15) NOT NULL UNIQUE,
	userscoretot integer(5) DEFAULT 0,
	idate datetime NOT NULL DEFAULT NOW(),
	ip varchar(15) NOT NULL,
	userflag integer(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (useridx)
);

DESC users;

-- ``````````````````````````````````````````````````````````````


-- --------------------------------------------------------------
-- 	chatlog table
-- 	pri key: useridx / not null unique: username
-- --------------------------------------------------------------

DROP TABLE IF EXISTS chatlog;
CREATE TABLE IF NOT EXISTS chatlog(
	logidx integer NOT NULL AUTO_INCREMENT,
	logcontents varchar(512) NOT NULL,
	logusername varchar(15) NOT NULL UNIQUE,
	logflag integer(1) NOT NULL DEFAULT 0,
	CONSTRAINT log_usr_name FOREIGN KEY(logusername) REFERENCES users(username),
	PRIMARY KEY (logidx)
);




-- ==============================================================================

--	idx별 typenanme 출력

-- SELECT wordtype.typename FROM wordtype;

-- typeidx별? 단어 출력

-- SELECT words.word FROM wordtype inner join words ON wordtype.typeidx = words.typeidx WHERE wordtype.typeidx = ?;

-- insert
 desc users;
-- select COUNT(selN.username) cnt_usrname from (select username from users) selN;

-- insert user
-- insert into users(username, ip) values(?, ?);

select * from users;

-- update user's score
-- update users set userscoretot = userscoretot + 10 where username = '김두한';

-- delete user
delete from users where username = '김두한';

SELECT wordtype.typename FROM wordtype;

SELECT typename FROM wordtype WHERE 1 = 1;

update users set username = 'dfdfdf10' where username = 'aaaa';

update users set username = 'wjddlrladnjs1' where username = 'wjddlrladnjs';