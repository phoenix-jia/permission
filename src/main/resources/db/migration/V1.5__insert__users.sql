insert into `v2_saas_users`
select `id`, `created_at`, `updated_at`, `deleted_at`, `username`, `password`, `name`, `phone`
, `position`, `platform`, `expire_at`, `resident_id`, `area_level`, `home_page_type` from saas_users;

insert into `v2_saas_user_comms`(`id`, `created_at`, `updated_at`, `comm_id`, `saas_user_id`)
select `id`, `created_at`, `updated_at`, `comm_id`, `saas_user_id` from saas_user_comms;

insert into `v2_saas_user_comm_alarms`(`id`, `created_at`, `updated_at`, `saas_user_id`, `comm_alarm_id`)
select `id`, `created_at`, `updated_at`, `saas_user_id`, `comm_alarm_id` from saas_user_comm_alarms;