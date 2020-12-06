select *
from records;

select *
from record_insts;

select r.rcd_id,
       ri.rcd_inst_id,
       rcd_name,
       r.type,
       created_time,
       modified_time,
       r.derived,
       t.tch_name,
       t.email,
       ri.content,
       r.base_content,
       comments
from records as r
         join record_insts ri on r.rcd_id = ri.rcd_id
         join teachers t on r.creator_id = t.role_id
where proj_id = 1
  and ri.role_id = 1
order by rcd_id;


select string_agg(coe || '#' || name, ',' order by derived_id) as derived
from (select r.rcd_id as result_id, r2.rcd_id as derived_id, r2.rcd_name as name, coefficient as coe
      from records as r
               join records_derived_rt rdr on r.rcd_id = rdr.rcd_id
               join records as r2 on rdr.derived_id = r2.rcd_id) t