create table if not exists public.history
(
    id             serial
    primary key,
    operation_type varchar(255),
    operation_date timestamp,
    employee_id    integer
    constraint fk_address_employee references public.users

    );