drop table if exists project_student_rt;

create table project_student_rt
(
    proj_std_id serial primary key,
    proj_id int,
    std_id int
);

alter table project_student_rt
    drop
        constraint project_student_rt__proj_id__fkey;
alter table project_student_rt
    add
        constraint project_student_rt__proj_id__fkey
            foreign key (proj_id) references projects(proj_id);

alter table project_student_rt
    drop
        constraint project_student_rt__std_id__fkey;
alter table project_student_rt
    add
        constraint project_student_rt__std_id__fkey
            foreign key (std_id) references students (std_id);