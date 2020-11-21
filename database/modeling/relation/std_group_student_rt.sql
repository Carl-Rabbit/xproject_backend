drop table if exists std_group_student_rt;

create table std_group_student_rt
(
    sgp_std_rt_id serial primary key,
    sgp_id int not null
        constraint std_group_student_rt_sgp_id_fkey references std_groups (sgp_id),
    std_id int not null
        constraint std_group_student_rt_std_id_fkey references students(std_id)
--     script char not null,      -- J: join, Q: quit, R: request, C: create, D: dissolve
--     opt_time timestamp not null default now()
)