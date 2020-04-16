CREATE TABLE `user` (
	`id` INT(11) NOT NULL,
	`create_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`update_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`telphone` VARCHAR(40) NOT NULL COLLATE 'utf8_unicode_ci',
	`password` VARCHAR(200) NOT NULL COLLATE 'utf8_unicode_ci',
	`nick_name` VARCHAR(40) NOT NULL COLLATE 'utf8_unicode_ci',
	`gender` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `telphone_unique_index` (`telphone`) USING BTREE
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `seller` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(80) NOT NULL DEFAULT '' COLLATE 'utf8_unicode_ci',
	`create_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`update_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`remark_score` DECIMAL(2,1) NOT NULL DEFAULT '0.0',
	`disabled_flag` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;


CREATE TABLE `category` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`create_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`update_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
	`name` VARCHAR(20) NOT NULL DEFAULT '' COLLATE 'utf8_unicode_ci',
	`icon_url` VARCHAR(200) NOT NULL DEFAULT '' COLLATE 'utf8_unicode_ci',
	`sort` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `name` (`name`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;
