insert into `v2_saas_user_roles`(`saas_role_id`, `saas_user_id`, `created_at`, `updated_at`, `deleted_at`)
select `saas_role_id`, `saas_user_id`,`created_at`, `updated_at`, `deleted_at` from saas_user_roles where saas_role_id != 3;

insert into `v2_saas_user_roles`(`saas_role_id`, `saas_user_id`, `created_at`, `updated_at`, `deleted_at`)
select 16 as `saas_role_id`, `saas_user_id`,`created_at`, `updated_at`, `deleted_at` from saas_user_roles where saas_role_id = 3