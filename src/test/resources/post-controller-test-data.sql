INSERT INTO `Users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES (1,'ljy5314@gmail.com', 'buckshot', 'Korea', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa', 'ACTIVE',  0);
INSERT INTO `Users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES (2,'ljy531@gmail.com', 'bug', 'Korea', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab', 'PENDING',  0);
INSERT INTO `Posts` (`id`, `content`, `created_at`, `modified_at`, `user_id`)
values (1, 'Hello, World', 1730026042850, 1730026042857,1);