

DROP TABLE IF EXISTS APPOINTMENTTABLE, EVENTTABLE, GROUPTABLE, TEMPUSERTABLE, COMPLETED, COURSE, FILESTORAGE, USERTABLE;

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(255),
    FIRST_NAME varchar(40),
    LAST_NAME varchar(40),
    UCO_ID INT(8),
    MAJOR varchar(45),
    ADVISEMENT_STATUS varchar(40) DEFAULT 'Not Selected',
    PHONE_NUMBER varchar(13),
    primary key (id)
);
create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (id)
);
create table TEMPUSERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(255),
    FIRST_NAME varchar(40),
    LAST_NAME varchar(40),
    UCO_ID varchar(10),
    MAJOR varchar(45),
    ADVISEMENT_STATUS varchar(20),
    PHONE_NUMBER varchar(13),
    code varchar(5),
    primary key (id)
);
-- Store the advisor events they make from the calendar 
create table EVENTTABLE(
    ID INT NOT NULL AUTO_INCREMENT,
    TITLE VARCHAR(20),
    ADVISOR_ID INT NOT NULL,
    START_DATE DATETIME NOT NULL,
    END_DATE DATETIME NOT NULL,
    primary key (ID),
    foreign key (ADVISOR_ID)
        references USERTABLE(ID)
);
-- Store the appoints that are made from an event the advisor makes
create table APPOINTMENTTABLE(
    ID INT NOT NULL AUTO_INCREMENT,
    EVENT_ID INT NOT NULL,
    APPOINTMENT_TIME DATETIME NOT NULL,
    BOOKED TINYINT DEFAULT 0,
    STUDENT_ID INT,
    primary key (ID),
    foreign key (EVENT_ID)
        references EVENTTABLE(ID),
    foreign key (STUDENT_ID)
        references USERTABLE(ID)
);


CREATE TABLE COURSE(
    COURSE_TYPE VARCHAR(4),
    COURSE_NUM  VARCHAR(4),
    COURSE_NAME VARCHAR(52),
    PRIMARY KEY(COURSE_TYPE, COURSE_NUM)
);

/*CREATE TABLE PREREQ(
    COURSE_NUM VARCHAR(8),
    PRE_REQ VARCHAR(8),
    
    CONSTRAINT PRE_REQ_PK PRIMARY KEY (COURSE_NUM, PRE_REQ),
    FOREIGN KEY (COURSE_NUM)    REFERENCES COURSE,
    FOREIGN KEY (PRE_REQ)       REFERENCES COURSE
);*/


CREATE TABLE COMPLETED(
    STUDENT_ID INT(8),
    COURSE_TYPE VARCHAR(4),
    COURSE_NUM VARCHAR(4),

    FOREIGN KEY STUDENT_FK (STUDENT_ID)
        REFERENCES USERTABLE(ID),
    FOREIGN KEY COURSE_FK (COURSE_TYPE, COURSE_NUM)
        REFERENCES COURSE(COURSE_TYPE, COURSE_NUM)
);

--Store Profile Pictures 
create table FILESTORAGE (
    FILE_ID INT NOT NULL,
    FILE_NAME VARCHAR(255),
    FILE_TYPE VARCHAR(255),
    FILE_SIZE BIGINT,
    FILE_CONTENTS BLOB,  /* binary data */
    PRIMARY KEY (FILE_ID)

);
/*
    initial entries
    root (password='ppp'): advisorgroup,studentgroup
    admin (password='ppp'): advisorgroup
    john (password='ppp'): studentgroup
*/
insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('root',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'root@uco.edu', 'Amy', 'Handcock', 43456712, '', '', '405-555-3456');
insert into GROUPTABLE (groupname, username) values ('advisorgroup', 'root');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'root');
insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('admin',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'admin@uco.edu', '', '', 99999999, '', '', '');
insert into GROUPTABLE (groupname, username) values ('advisorgroup', 'admin');

-- ****************************************************************************************
-- ****************************   Students   ****************************
-- ****************************************************************************************
insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('john@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'john@uco.edu', 'John', 'Grunt', 34565412, '6100 - Computer Science ', default,
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'john@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('a@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'a@uco.edu', 'A', 'Apple', 34565412, '6100 - Computer Science ', '2017-04-28 08:30:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'a@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('b@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'b@uco.edu', 'B', 'Boat', 34565412, '6100 - Computer Science ', '2017-04-28 08:50:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'b@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('c@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'c@uco.edu', 'C', 'Cat', 34565412, '6100 - Computer Science ', '2017-04-28 09:00:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'c@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('d@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'd@uco.edu', 'D', 'Dog', 34565412, '6100 - Computer Science ', '2017-04-28 09:20:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'd@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('e@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'e@uco.edu', 'E', 'Egg', 34565412, '6100 - Computer Science ', '2017-04-28 16:00:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'e@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('f@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'f@uco.edu', 'F', 'Fish', 34565412, '6100 - Computer Science ', '2017-04-28 16:10:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'f@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('g@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'g@uco.edu', 'G', 'Grape', 34565412, '6100 - Computer Science ', '2017-04-28 16:20:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'g@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('h@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'h@uco.edu', 'H', 'Hat', 34565412, '6100 - Computer Science ', '2017-04-25 12:40:00',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'h@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('i@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'i@uco.edu', 'I', 'Ice', 34565412, '6100 - Computer Science ', default,
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'i@uco.edu');

insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('j@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'j@uco.edu', 'J', 'Jam', 34565412, '6100 - Computer Science ', default,
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'j@uco.edu');

-- ****************************************************************************************
-- ****************************   Events for the admin login   ****************************
-- ****************************************************************************************
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('Afternoon', 2, '2017-04-5 12:30:00', '2017-04-5 13:00:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('Before Meeting', 2, '2017-04-28 08:30:00', '2017-04-28 09:30:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('After Class', 2, '2017-04-28 16:00:00', '2017-04-28 17:00:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('After Lunch', 2, '2017-04-25 12:30:00', '2017-04-25 13:00:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('Morning', 2, '2017-04-25 08:00:00', '2017-04-25 08:30:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('Afternoon', 2, '2017-05-2 13:30:00', '2017-05-2 14:00:00');
insert into EVENTTABLE (title, advisor_id, start_date, end_date)
    values ('Before Lunch', 2, '2017-05-5 11:00:00', '2017-05-5 11:30:00');

-- From the events in admin login
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-15 12:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-15 12:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-15 12:50:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 08:30:00', 1, 4);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 08:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 08:50:00', 1, 5);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 09:00:00', 1, 6);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 09:10:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 09:20:00', 1, 7);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 09:30:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 16:00:00', 1, 8);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 16:10:00', 1, 9);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-28 16:20:00', 1, 10);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 16:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 16:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-28 16:50:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-25 12:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked, student_id)
    values (2, '2017-04-25 12:40:00', 1, 11);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-25 12:50:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-25 08:00:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-25 08:10:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-04-25 08:20:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-2 13:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-2 13:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-2 13:50:00', 0);

insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-5 11:00:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-5 11:10:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values (2, '2017-05-5 11:20:00', 0);

/*********************************************************************************************************
INSERT COURSES
*********************************************************************************************************/
INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1053','Professional Computer Applications & Problem Solving');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1103','Intro to Computing Systems');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1513','Beginning Programming');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1521','Beginning Programming Lab');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1613','Programming I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','1621','Programming I Lab');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','2123','Discrete Structures');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','2413','Visual Programming');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','2613','Programming II');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','2621','Programming II Lab');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','2833','Computer Organization I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3000','Workshop in Computer Science');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('SE','3103','Object Oriented SW Design & Construct');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3303','Systems Analysis Design');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3413','Enterprise Programming');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3613','Data Structures & Algorithms');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3621','Data Structures & Algorithms Lab');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','3833','Computer Organization II');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4003','Applictions of Database Management Systems');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4023','Programming Languages');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4063','Networks');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4133','Concepts of Artifical Intelligence');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4153','Operating Systems');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4173','Translator Design');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4273','Theory of Computing');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('SE','4283','Software Engineering I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4303','Mobile Application Programming');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4323','Computer & Network Security');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4373','Web Server Programming');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4383','File Structures');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4401','Ethics in Computing');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('SE','4423','Software Engineering II');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('SE','4433','Software Architecture & Design');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4513','Software Design & Development');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('SE','4513','Software Engineering Senior Project');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4910','Seminar in Computer Science');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4930','Individual Study');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','4950','Internship in Computer Science');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5043','Applied Database Management');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5053','Operating Systems');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5063','Networks');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5283','Software Engineering');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5023','Programming Languages');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5273','Theory of Computing');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5980','Graduate Project');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('CMSC','5990','Graduate Thesis');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','5113','Operations Research I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','5143','Advanced Calculus for Applications I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','5853','Introduction to Graduate Research');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('STAT','5263','Computer Applications in Statistics');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','5980','Graduate Project');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','5990','Graduate Thesis');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','2313','Calculus I');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','2323','Calculus II');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','2333','Calculus III');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('MATH','3143','Linear Algebra');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('STAT','2113','Statistical Methods');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('STAT','2103','Introduction to Statistics for Sciences');

INSERT INTO COURSE (COURSE_TYPE, COURSE_NUM, COURSE_NAME)
VALUES ('STAT','4113','Mathematical Statistics I');


/*********************************************************************************************************
INSERT COMPLETED
*********************************************************************************************************/
INSERT INTO COMPLETED (STUDENT_ID, COURSE_TYPE, COURSE_NUM)
VALUES((SELECT ID FROM USERTABLE WHERE USERNAME='john@uco.edu'),'STAT', '2103');

INSERT INTO COMPLETED (STUDENT_ID, COURSE_TYPE, COURSE_NUM)
VALUES((SELECT ID FROM USERTABLE WHERE USERNAME='john@uco.edu'),'CMSC', '1513');

INSERT INTO COMPLETED (STUDENT_ID, COURSE_TYPE, COURSE_NUM)
VALUES((SELECT ID FROM USERTABLE WHERE USERNAME='john@uco.edu'),'CMSC', '1613');

