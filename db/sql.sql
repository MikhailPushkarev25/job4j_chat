

create table roles (
id serial primary key,
roles varchar(100)
);

create table users (
id serial primary key,
username varchar(64),
password varchar(64),
role_id int references roles(id)
);

create table message (
id serial primary key,
description varchar(1000),
created timestamp,
room_id int references room(id)
);

create table room (
id serial primary key,
names varchar(200)
);

insert into users(username, password, role_id) values ('mikhail', '123', 1);
insert into users(username, password, role_id) values ('Roman', '123456', 2);

insert into roles(roles) values('ROLE_USE');
insert into roles(roles) values ('ROLE_ROMAN');

insert into message(description, created, room_id) values('Сегодня был тяжелый день', '2022-01-26T18:01:20.166+00:00', 1);
insert into message(description, created, room_id) values ('Получил оффер!', '2022-01-26T18:01:20.166+00:00', 2);

insert into room(names) values('Нужно больше отдыхать!');
insert into room(names) values('Поздравляю!');