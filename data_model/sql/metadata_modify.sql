-- Get isp names from different datasources for usage in isp_mappings table
-- get according to page size and page limit.

select C.country_name as country_name,I.isp_name as isp_name,I.data_source_name as data_source_name from isp as I,isp_mappings as M,country as C ,(select concat(C.country_name as country_name,'|',I.isp_name as isp_name) as country_ispname_val from isp as I,isp_mappings as M,country as C where I.country_id = C.id and I.isp_name = M.isp_name and C.country_name = M.country_name) as T where I.country_id = C.id and concat(C.country_name,'|',I.isp_name) != T.country_ispname_val limit ?,?

-- For edit of isp_mappings.
select country_name,isp_ui_name,isp_name from isp_mappings  limit ?,?;

-- Use above data for isp_mappings insertion/updation.
-- If isp_mappings has isp_ui_name as null then insert otherwise update...
-- Use string buffer to prepare data for $DATA_PREPARED_IN_APP using country_name,isp_name from above two.
-- in one shot insert would enter max of page size entries.
insert into isp_mappings (country_name,isp_ui_name,isp_name) values $DATA_PREPARED_IN_APP;

update isp_mappings set isp_ui_name = ? where country_name = ? and isp_name = ?;
