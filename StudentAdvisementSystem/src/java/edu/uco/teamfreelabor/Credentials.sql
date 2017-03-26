/* create the following table
   in WSP database
*/


DROP TABLE USERTABLE;
DROP TABLE GROUPTABLE;


create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(255),
    FIRST_NAME varchar(40),
    LAST_NAME varchar(40),
    UCO_ID varchar(10),
    MAJOR varchar(20),
    ADVISEMENT_STATUS varchar(20),
    PHONE_NUMBER varchar(13),
    primary key (id)
);

create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (id)
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
    values ('john',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7',
        'john@uco.edu', 'John', 'Grunt', '34565412', 'CS 6100', 'Pending',
        '405-555-1111');
insert into GROUPTABLE (groupname, username) values ('studentgroup', 'john');


