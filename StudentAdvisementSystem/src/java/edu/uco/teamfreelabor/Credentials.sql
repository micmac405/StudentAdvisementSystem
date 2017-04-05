/* create the following table
   in WSP database
*/
DROP TABLE IF EXISTS APPOINTMENTTABLE, EVENTTABLE, USERTABLE, GROUPTABLE, TEMPUSERTABLE, PREREQ, COURSE;

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(255),
    FIRST_NAME varchar(40),
    LAST_NAME varchar(40),
    UCO_ID varchar(10),
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
    ADVISOR_ID INT NOT NULL,
    START_DATE DATETIME NOT NULL,
    END_DATE DATETIME NOT NULL,
    primary key (ID),
    foreign key (ADVISOR_ID)
        references USERTABLE(ID)
);
-- Store the appoints that are made from an event
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
    ID INT NOT NULL AUTO_INCREMENT,
    COURSE_NUM VARCHAR2(8) PRIMARY KEY,
    COURSE_NAME VARCHAR2(52)
);

CREATE TABLE PREREQ(
    ID INT NOT NULL AUTO_INCREMENT,
    COURSE_NUM VARCHAR2(8),
    PRE_REQ VARCHAR2(8),
    
    CONSTRAINT PRE_REQ_PK PRIMARY KEY (COURSE_NUM, PRE_REQ),
    FOREIGN KEY (COURSE_NUM)    REFERENCES COURSE,
    FOREIGN KEY (PRE_REQ)       REFERENCES COURSE
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
        'root@uco.edu', 'Amy', 'Handcock', '43456712', '', '', '405-555-3456');
insert into GROUPTABLE (groupname, username) values ('advisorgroup', 'root');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'root');
insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('admin',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'admin@uco.edu', '', '', '', '', '', '');
insert into GROUPTABLE (groupname, username) values ('advisorgroup', 'admin');
insert into USERTABLE (username, password, email, first_name, last_name, uco_id, 
    major, advisement_status, phone_number)
    values ('john@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'john@uco.edu', 'John', 'Grunt', '34565412', '6100 - Computer Science ', default,
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'john@uco.edu');
insert into EVENTTABLE (advisor_id, start_date, end_date)
    values ((select id from usertable where id = 1), '2017-03-31 07:30:00', '2017-03-31 08:00:00');
insert into EVENTTABLE (advisor_id, start_date, end_date)
    values ((select id from usertable where id = 1), '2017-04-5 012:30:00', '2017-04-6 14:00:00');
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-03-31 07:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-03-31 07:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-03-31 07:50:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-03-30 08:50:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-04-15 07:30:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-04-15 07:40:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-04-05 07:50:00', 0);
insert into APPOINTMENTTABLE (event_id, appointment_time, booked)
    values ((select id from eventtable where id = 1), '2017-04-05 08:50:00', 0);

/*********************************************************************************************************
INSERT COURSES
*********************************************************************************************************/
INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1053','Professional Computer Applications & Problem Solving');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1103','Intro to Computing Systems');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1513','Beginning Programming');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1521','Beginning Programming Lab');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1613','Programming I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC1621','Programming I Lab');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC2123','Discrete Structures');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC2413','Visual Programming');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC2613','Programming II');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC2621','Programming II Lab');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC2833','Computer Organization I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3000','Workshop in Computer Science');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('SE3103','Object Oriented SW Design & Construct');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3303','Systems Analysis Design');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3413','Enterprise Programming');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3613','Data Structures & Algorithms');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3621','Data Structures & Algorithms Lab');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC3833','Computer Organization II');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4003','Applictions of Database Management Systems');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4023','Programming Languages');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4063','Networks');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4133','Concepts of Artifical Intelligence');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4153','Operating Systems');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4173','Translator Design');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4273','Theory of Computing');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('SE4283','Software Engineering I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4303','Mobile Application Programming');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4323','Computer & Network Security');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4373','Web Server Programming');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4383','File Structures');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4401','Ethics in Computing');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('SE4423','Software Engineering II');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('SE4433','Software Architecture & Design');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4513','Software Design & Development');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('SE4513','Software Engineering Senior Project');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4910','Seminar in Computer Science');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4930','Individual Study');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC4950','Internship in Computer Science');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5043','Applied Database Management');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5053','Operating Systems');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5063','Networks');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5283','Software Engineering');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5023','Programming Languages');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5273','Theory of Computing');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5980','Graduate Project');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('CMSC5990','Graduate Thesis');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH5113','Operations Research I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH5143','Advanced Calculus for Applications I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH5853','Introduction to Graduate Research');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('STAT5263','Computer Applications in Statistics');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH5980','Graduate Project');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH5990','Graduate Thesis');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH2313','Calculus I');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH2323','Calculus II');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH2333','Calculus III');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('MATH3143','Linear Algebra');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('STAT2113','Statistical Methods');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('STAT2103','Introduction to Statistics for Sciences');

INSERT INTO COURSE (COURSE_NUM, COURSE_NAME)
VALUES ('STAT4113','Mathematical Statistics I');
