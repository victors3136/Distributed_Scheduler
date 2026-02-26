CREATE DATABASE character_db;
CREATE DATABASE weapon_db;
CREATE DATABASE mapping_db;

\c character_db;
create table public.characters
(
    id               integer primary key    not null generated always as identity,
    display_name     character varying(255) not null,
    hp               integer                not null,
    attack_modifier  integer                not null,
    defence_modifier integer                not null
);
create unique index characters_display_name_key on characters using btree (display_name);

\c weapon_db;
create table public.weapons
(
    id           integer primary key    not null generated always as identity,
    display_name character varying(255) not null,
    damage       integer                not null
);
create unique index weapons_display_name_key on weapons using btree (display_name);

\c mapping_db;
create table public.mappings
(
    character integer not null,
    weapon    integer not null,
    primary key (character, weapon)
);
