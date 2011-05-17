--
-- Table structure for table calendar_portlet
--
DROP TABLE IF EXISTS calendar_portlet;
CREATE TABLE calendar_portlet
(
	id_portlet int NOT NULL,
	date_begin date default NULL,
	date_end date default NULL,
	code_agenda_name varchar(255) NOT NULL,
	number_days int default NULL,
	PRIMARY KEY (id_portlet, code_agenda_name)
);

--
-- Table structure for table calendar_mini_portlet
--
DROP TABLE IF EXISTS calendar_mini_portlet;
CREATE TABLE calendar_mini_portlet
(
	top_event smallint default 0 NOT NULL,
	PRIMARY KEY (top_event)
);
