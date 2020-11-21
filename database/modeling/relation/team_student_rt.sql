drop table if exists team_student_rt;

create table team_student_rt
(
    team_std_rt_id serial primary key not null,
    team_id int not null
        constraint team_student_rt_team_id_fkey references teams (team_id),
    std_id int not null
        constraint team_student_rt_std_id_fkey references students(std_id)
--     script char not null,      -- J: join, Q: quit, R: request, C: create, D: dissolve
--     opt_time timestamp not null default now()
)