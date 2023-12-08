alter table users drop isValid;

alter table users add if not exists is_valid boolean;
