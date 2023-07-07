create table user(
    id int auto_increment primary key,
    fname varchar(50) not null,
    lname varchar(50),
    email varchar(50) not null
);

create table user_availability(
    id int auto_increment primary key,
    _date date not null,
    _start time not null,
    _end time not null,
    userid int not null
);