select std_id, r.role_id, username, std_name, std_no, std_class, std.flags, sch_id, email
from project_insts as pi
    join proj_inst_student_rt pisr on pi.proj_inst_id = pisr.proj_inst_id
    join roles r on pisr.std_role_id = r.role_id
    join students as std on pisr.std_role_id = std.role_id
where pi.proj_inst_id = 1;


-- check student

select pi.*
from project_insts as pi
    join proj_inst_student_rt pisr on pi.proj_inst_id = pisr.proj_inst_id
where proj_id = 1 and std_role_id = 1;