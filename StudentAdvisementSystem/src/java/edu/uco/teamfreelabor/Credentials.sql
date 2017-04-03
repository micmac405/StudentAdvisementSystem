/* create the following table
   in WSP database
*/
DROP TABLE IF EXISTS APPOINTMENTTABLE, EVENTTABLE, USERTABLE, GROUPTABLE, TEMPUSERTABLE;
create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(255),
    FIRST_NAME varchar(40),
    LAST_NAME varchar(40),
    UCO_ID varchar(10),
    MAJOR varchar(45),
    ADVISEMENT_STATUS varchar(40) DEFAULT 'Not Selected',    PHONE_NUMBER varchar(13),
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