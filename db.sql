create database character_db;
\c character_db;
create table characters (
    id               int          not null primary key,
    display_name     varchar(255) not null unique,
    hp               int          not null,
    attack_modifier  int          not null,
    defence_modifier int          not null
);
create database weapon_db;
\c weapon_db;
create table weapons (
    id           int          not null primary key,
    display_name varchar(255) not null unique,
    damage       int          not null
);

create database mapping_db;
\c mapping_db;
create table mappings (
    character int not null,
    weapon    int not null,
    primary key(character, weapon)
);