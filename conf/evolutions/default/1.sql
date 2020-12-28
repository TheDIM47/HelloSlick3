# --- !Ups
create table if not exists ENTITY(ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR NOT NULL);

# --- !Downs
drop table ENTITY;
