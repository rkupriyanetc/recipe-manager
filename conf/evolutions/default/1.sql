# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table linkeds (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linkeds primary key (id))
;

create table recipes (
  id                        bigint auto_increment not null,
  title                     varchar(150) not null,
  description               longtext,
  creation_date             datetime not null,
  link                      varchar(255),
  user_id                   bigint,
  image_name                varchar(36),
  is_private                tinyint(1) default 0,
  visiting                  integer,
  rating                    tinyint,
  constraint pk_recipes primary key (id))
;

create table tokens (
  id                        bigint auto_increment not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   datetime,
  expires                   datetime,
  constraint ck_tokens_type check (type in ('EV','PR')),
  constraint uq_tokens_token unique (token),
  constraint pk_tokens primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(60),
  name                      varchar(25) not null,
  fullname                  varchar(60),
  first_name                varchar(40),
  last_name                 varchar(20),
  telephone_number          varchar(15),
  address                   varchar(255),
  last_login                datetime,
  active                    tinyint(1) default 0,
  email_validated           tinyint(1) default 0,
  constraint pk_users primary key (id))
;

create table permissions (
  id                        bigint auto_increment not null,
  value                     varchar(255),
  constraint pk_permissions primary key (id))
;

create table roles (
  id                        bigint auto_increment not null,
  role_name                 varchar(8),
  constraint pk_roles primary key (id))
;


create table users_roles (
  users_id                       bigint not null,
  roles_id                       bigint not null,
  constraint pk_users_roles primary key (users_id, roles_id))
;

create table users_permissions (
  users_id                       bigint not null,
  permissions_id                 bigint not null,
  constraint pk_users_permissions primary key (users_id, permissions_id))
;
alter table linkeds add constraint fk_linkeds_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linkeds_user_1 on linkeds (user_id);
alter table recipes add constraint fk_recipes_user_2 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_recipes_user_2 on recipes (user_id);
alter table tokens add constraint fk_tokens_targetUser_3 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_tokens_targetUser_3 on tokens (target_user_id);



alter table users_roles add constraint fk_users_roles_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_roles add constraint fk_users_roles_roles_02 foreign key (roles_id) references roles (id) on delete restrict on update restrict;

alter table users_permissions add constraint fk_users_permissions_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_permissions add constraint fk_users_permissions_permissions_02 foreign key (permissions_id) references permissions (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table linkeds;

drop table recipes;

drop table tokens;

drop table users;

drop table users_roles;

drop table users_permissions;

drop table permissions;

drop table roles;

SET FOREIGN_KEY_CHECKS=1;

