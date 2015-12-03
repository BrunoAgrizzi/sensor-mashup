# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table resource (
  oid                       bigint not null,
  resource_token            varchar(255),
  label                     varchar(255),
  sensor_oid                bigint,
  constraint pk_resource primary key (oid))
;

create table resource_data (
  oid                       bigint not null,
  value                     double,
  timestamp                 bigint,
  resource_oid              bigint,
  constraint pk_resource_data primary key (oid))
;

create table sensor (
  oid                       bigint not null,
  sensor_token              varchar(255),
  label                     varchar(255),
  constraint pk_sensor primary key (oid))
;

create sequence resource_seq;

create sequence resource_data_seq;

create sequence sensor_seq;

alter table resource add constraint fk_resource_sensor_1 foreign key (sensor_oid) references sensor (oid) on delete restrict on update restrict;
create index ix_resource_sensor_1 on resource (sensor_oid);
alter table resource_data add constraint fk_resource_data_resource_2 foreign key (resource_oid) references resource (oid) on delete restrict on update restrict;
create index ix_resource_data_resource_2 on resource_data (resource_oid);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists resource;

drop table if exists resource_data;

drop table if exists sensor;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists resource_seq;

drop sequence if exists resource_data_seq;

drop sequence if exists sensor_seq;

