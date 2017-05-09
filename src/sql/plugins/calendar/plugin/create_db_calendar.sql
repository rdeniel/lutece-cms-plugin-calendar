--
-- Table structure for table calendar_agendas
--
DROP TABLE IF EXISTS calendar_agendas;
CREATE TABLE calendar_agendas
(
	id_agenda int default 0 NOT NULL,
	agenda_name varchar(130) default NULL,
	agenda_image varchar(130) default NULL,
	agenda_prefix varchar(130) default NULL,
	role varchar(130) NOT NULL,
	workgroup_key varchar(50) default NULL,
	role_manage varchar(130) default NULL,
	is_notify smallint NOT NULL DEFAULT 0,
	period_validity int DEFAULT -1,
	PRIMARY KEY (id_agenda)
);

--
-- Table structure for table calendar_events
--
DROP TABLE IF EXISTS calendar_events;
CREATE TABLE calendar_events
(
	id_event int NOT NULL,
	id_agenda int default 0 NOT NULL,
	event_date date NOT NULL,
	event_date_end date default NULL,
	event_time_start varchar(5) default NULL,
	event_time_end varchar(5) default NULL,
	event_title varchar(60) NOT NULL,
	event_date_occurence int default NULL,
	event_date_periodicity int default NULL,
	event_date_creation timestamp default CURRENT_TIMESTAMP NOT NULL,
	event_excluded_day varchar(13) default NULL,
	PRIMARY KEY (id_event)
);

--
-- Table structure for table calendar_events_occurrences
--
DROP TABLE IF EXISTS calendar_events_occurrences;
CREATE TABLE calendar_events_occurrences
(
	id_occurrence int NOT NULL,
	id_event int NOT NULL,
	id_agenda int NOT NULL,
	occurrence_date date NOT NULL,
	occurrence_time_start varchar(5) default NULL,
	occurrence_time_end varchar(5) default NULL,
	occurrence_title varchar(60) NOT NULL,
	occurrence_status varchar(20) default NULL,
	PRIMARY KEY (id_occurrence)
);

--
-- Table structure for table calendar_events_features
--
DROP TABLE IF EXISTS calendar_events_features;
CREATE TABLE calendar_events_features
(
	id_feature int NOT NULL,
	id_event int NOT NULL,
	feature_description long varchar NOT NULL,
	feature_tags varchar(130) default NULL,
	feature_location varchar(60) default NULL,
	feature_location_town varchar(60) default NULL,
	feature_location_zip varchar(20) default NULL,
	feature_location_address varchar(130) default NULL,
	feature_map_url varchar(130) default NULL,
	feature_link_url varchar(130) default NULL,
	document_id int default NULL,
	feature_page_url varchar(300) default NULL,
	feature_top_event int default 0,
	feature_image long varbinary default NULL,
	image_mime_type varchar(100) default NULL,
	PRIMARY KEY (id_feature)
);

--
-- Table structure for table calendar_export_stylesheets
--
DROP TABLE IF EXISTS calendar_export_stylesheets;
CREATE TABLE calendar_export_stylesheets
(
	id_stylesheet int default 0 NOT NULL,
	description varchar(255),
	file_name varchar(255),
	source long varbinary,
	extension varchar(5),
	PRIMARY KEY (id_stylesheet)
);
--
-- Table structure for table calendar_subscriber_details
--
DROP TABLE IF EXISTS calendar_subscriber_details;
CREATE TABLE calendar_subscriber_details
(
	id_subscriber int default 0 NOT NULL,
	email varchar(100) default NULL,
	PRIMARY KEY (id_subscriber)
);

--
-- Table structure for table calendar_subscriber
--
DROP TABLE IF EXISTS calendar_subscriber;
CREATE TABLE calendar_subscriber
(
	id_subscriber int default 0 NOT NULL,
	id_agenda int default 0 NOT NULL,
	date_subscription timestamp default CURRENT_TIMESTAMP NOT NULL,
	PRIMARY KEY (id_subscriber, id_agenda)
);

--
-- Table structure for table calendar_category
--
DROP TABLE IF EXISTS calendar_category;
CREATE TABLE calendar_category
(
	id_category int NOT NULL,
	calendar_category_name varchar(100) NOT NULL,
	description varchar(255) default NULL,
	icon_content long varbinary default NULL,
	icon_mime_type varchar(100) default NULL,
	workgroup_key varchar(50) default NULL,
	PRIMARY KEY (id_category)	
);

--
-- Table structure for table calendar_category_link
--
DROP TABLE IF EXISTS calendar_category_link;
CREATE TABLE calendar_category_link
(
	id_event int NOT NULL,
	id_category int NOT NULL,
	PRIMARY KEY (id_event, id_category)
);

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
-- Table structure for calendar_notify_key
--
DROP TABLE IF EXISTS calendar_notify_key;
CREATE TABLE calendar_notify_key
(
	key_email varchar(255) NOT NULL,
	email varchar(100) default NULL,
	id_agenda int DEFAULT NULL,
	date_expiry timestamp DEFAULT NULL NULL,
	PRIMARY KEY  (key_email)
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
