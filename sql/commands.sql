create table QUIZ(id bigint auto_increment, name varchar(255));

select ID, NAME from QUIZ


insert into QUIZ (name) values ('Test Quiz')

UPDATE QUIZ SET NAME='Quiz Test 2' WHERE ID = 1;
DELETE QUIZ  WHERE ID = 1;