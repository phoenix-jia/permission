insert into v2_login_records (created_at, updated_at, username, client_ip, client_address, project_code)
select  created_at, updated_at, username, client_ip, client_address, project_code from login_records