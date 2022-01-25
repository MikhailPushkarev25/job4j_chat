

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
created timestamp
);

create table room (
id serial primary key,
names varchar(200)
);

insert into users(username, password, role_id) values ('mikhail', '123', 1);

insert into roles(roles) values('ROLE_USE');

insert into message(description, created) values('Сегодня был тяжелый день', '2018-01-01');

insert into room(names) values('Расскажите как у вас прошел день!');