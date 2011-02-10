--
-- Table structure for table calendar_events_users
--
DROP TABLE IF EXISTS calendar_events_users;
CREATE TABLE calendar_events_users
(
	id_event int NOT NULL,
	user_login VARCHAR(100) NOT NULL,
	PRIMARY KEY (id_event, user_login)
);

--
-- Update table calendar_events
--
ALTER TABLE calendar_events ADD COLUMN event_excluded_day varchar(13) default NULL;

--
-- Upgrade table calendar_agendas
--
ALTER TABLE calendar_agendas ADD COLUMN is_notify smallint NOT NULL DEFAULT 0;
ALTER TABLE calendar_agendas ADD COLUMN period_validity int DEFAULT -1;

--
-- Table structure for table calendar_events_occurences
--
DROP TABLE IF EXISTS calendar_notify_key;
CREATE TABLE calendar_notify_key 
(
	key_email varchar(255) DEFAULT NULL, 
	email varchar(100) default NULL,
	id_agenda int DEFAULT NULL,
	date_expiry timestamp DEFAULT NULL NULL,
	PRIMARY KEY (key_email)
);

--
-- Table structure for table calendar_parameter
--
DROP TABLE IF EXISTS calendar_parameter;
CREATE TABLE calendar_parameter
(
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);

--
-- Init table calendar_parameter
--
INSERT INTO calendar_parameter (parameter_key, parameter_value) VALUES ('dashboard_n_next_days', '3');
INSERT INTO calendar_parameter (parameter_key, parameter_value) VALUES ('dashboard_nb_events', '3');
