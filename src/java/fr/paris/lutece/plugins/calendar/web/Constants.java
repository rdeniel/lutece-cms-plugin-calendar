/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.calendar.web;

import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * This class provides contants for the calendar plugin.
 */
public final class Constants
{
    // Markers
    public static final String MARK_AGENDA = "agenda";
    public static final String MARK_WEEKS = "weeks";
    public static final String MARK_DAY = "day";
    public static final String MARK_DAYS = "days";
    public static final String MARK_DAY_CLASS = "day_class";
    public static final String MARK_LINK_CLASS = "link_class";
    public static final String MARK_MONTH_LABEL = "month_label";
    public static final String MARK_JSP_URL = "jsp_url";
    public static final String MARK_EVENT_TITLE = "event_title";
    public static final String MARK_EVENT_SHORT_TITLE = "short_event_title";
    public static final String MARK_EVENT_DESCRIPTION = "event_description";
    public static final String MARK_EVENT_LOCATION = "event_location";
    public static final String MARK_EVENT_IMAGE = "event_image";
    public static final String MARK_EVENT_URL = "event_url";
    public static final String MARK_EVENT_ID = "event_id";
    public static final String MARK_DAY_LINK = "day_link";
    public static final String MARK_EVENTS = "events";
    public static final String MARK_ACTION = "action";
    public static final String MARK_PAGE = "page";
    public static final String MARK_QUERY = "query";
    public static final String MARK_DATE_START = "date_start";
    public static final String MARK_DATE_END = "date_end";
    public static final String MARK_PERIOD = "period";
    public static final String MARK_SUBSCRIPTION_PAGE = "subscription_page";
    public static final String MARK_EMAIL_FRIEND_PAGE = "email_friend_page";
    public static final String MARK_DOWNLOAD_PAGE = "download_page";
    public static final String MARK_RSS_PAGE = "rss_page";
    public static final String MARK_AGENDA_NAME = "agenda_name";
    public static final String MARK_CATEGORY_LIST = "category_list";
    public static final String MARK_CATEGORY_DEFAULT_LIST = "category_default_list";
    public static final String MARK_PERMISSION_ADVANCED_PARAMETER = "permission_advanced_parameter";
    public static final String MARK_URL = "url";
    public static final String MARK_ICON = "icon";
    public static final String MARK_NB_SUBSCRIBERS_LIST = "nb_subscribers_list";
    public static final String MARK_CALENDAR_PARAMETERS = "calendar_parameters";
    public static final String MARK_NB_EVENTS_MAX = "nb_events_max";
    public static final String MARK_IS_AUTHORIZED = "is_authorized";

    // Parameters
    public static final String PARAM_VIEW = "view";
    public static final String PARAM_DATE = "date";
    public static final String PARAM_AGENDA = "agenda"; //FIXME in contradiction with PARAMETER_CALENDAR_ID
    public static final int PARAM_DAY = 1;
    public static final int PARAM_WEEK = 7;
    public static final int PARAM_MONTH = 30;

    // Properties
    public static final String PLUGIN_NAME = "calendar";
    public static final String PLUGIN_JCAPTCHA = "jcaptcha";
    public static final String PROPERTY_TITLE = "calendar.title";
    public static final String PROPERTY_PATH = "calendar.path";
    public static final String PROPERTY_SHORTLABEL_MONDAY = "calendar.shortlabel.monday";
    public static final String PROPERTY_SHORTLABEL_TUESDAY = "calendar.shortlabel.tuesday";
    public static final String PROPERTY_SHORTLABEL_WEDNESDAY = "calendar.shortlabel.wednesday";
    public static final String PROPERTY_SHORTLABEL_THURSDAY = "calendar.shortlabel.thursday";
    public static final String PROPERTY_SHORTLABEL_FRIDAY = "calendar.shortlabel.friday";
    public static final String PROPERTY_SHORTLABEL_SATURDAY = "calendar.shortlabel.saturday";
    public static final String PROPERTY_SHORTLABEL_SUNDAY = "calendar.shortlabel.sunday";
    public static final String PROPERTY_LABEL_MONDAY = "calendar.label.monday";
    public static final String PROPERTY_LABEL_TUESDAY = "calendar.label.tuesday";
    public static final String PROPERTY_LABEL_WEDNESDAY = "calendar.label.wednesday";
    public static final String PROPERTY_LABEL_THURSDAY = "calendar.label.thursday";
    public static final String PROPERTY_LABEL_FRIDAY = "calendar.label.friday";
    public static final String PROPERTY_LABEL_SATURDAY = "calendar.label.saturday";
    public static final String PROPERTY_LABEL_SUNDAY = "calendar.label.sunday";
    public static final String PROPERTY_LABEL_FORMAT_DAY = "calendar.label.format.day";
    public static final String PROPERTY_LABEL_FORMAT_MONTH = "calendar.label.format.month";
    public static final String PROPERTY_LABEL_FORMAT_DATE_OF_DAY = "calendar.label.format.date.day";
    public static final String PROPERTY_LABEL_FORMAT_WEEK_DAY = "calendar.label.format.week.day";
    public static final String PROPERTY_SMALLCALENDAR_LINKCLASS_NO_EVENT = "calendar.smallcalendar.linkclass.noevent";
    public static final String PROPERTY_SMALLCALENDAR_LINKCLASS_HAS_EVENTS = "calendar.smallcalendar.linkclass.hasevents";
    public static final String PROPERTY_EVENT_SHORT_TITLE_LENGTH = "calendar.event.short.title.length";
    public static final String PROPERTY_EVENT_SHORT_TITLE_END = "calendar.event.short.title.end";
    public static final String PROPERTY_AGENDASERVICE_CACHE_ENABLE = "calendar.agendaservice.cache.enable";
    public static final String PROPERTY_AGENDA = "calendar.agenda.";
    public static final String PROPERTY_RUNAPP_JSP_URL = "calendar.runapp.jsp.url";
    public static final String PROPERTY_EVENTLIST_VIEW_DAY = "calendar.view.day.eventlist";
    public static final String PROPERTY_EVENTLIST_VIEW_WEEK = "calendar.view.week.eventlist";
    public static final String PROPERTY_EVENTLIST_VIEW_MONTH = "calendar.view.month.eventlist";
    public static final String PROPERTY_EVENTLIST = "calendar.eventlist.";
    public static final String PROPERTY_WORKING_DAYS_IN_WEEK = "calendar.working.days";
    public static final String PROPERTY_ICAL_TRACE_ENABLE = "calendar.ical.trace.enable";
    public static final String PROPERTY_READ_ONLY = "calendar.resourceType.readOnly";
    public static final String PROPERTY_READ_WRITE = "calendar.resourceType.readAndWrite";
    public static final String PROPERTY_ACCESSIBLE_BY_USER = "accessible";
    public static final String PROPERTY_ROLE_NONE = "none";
    public static final String CONSTANT_SLASH = "/";
    public static final String PROPERTY_EVENT_DEFAULT_STATUS = "calendar.event.status.default";
    public static final int PROPERTY_PERIOD_NONE = 0;
    public static final int PROPERTY_PERIOD_TODAY = 1;
    public static final int PROPERTY_PERIOD_WEEK = 2;
    public static final int PROPERTY_PERIOD_RANGE = 3;
    public static final String PROPERTY_CALENDAR_NONE = "none";
    public static final String PROPERTY_SHORTCUT_TODAY = "calendar.portlet_mini_calendar.shortcut.today";
    public static final String PROPERTY_SHORTCUT_TOMORROW = "calendar.portlet_mini_calendar.shortcut.tomorrow";
    public static final String PROPERTY_SHORTCUT_WEEK = "calendar.portlet_mini_calendar.shortcut.week";
    public static final String PROPERTY_MODULE_CALENDAR = "module-calendar-document";
    public static final String PROPERTY_FRIEND_MAIL_TITLE = "calendar.email-friend.mail.title";
    public static final String PROPERTY_EVENT_STATUS_CONFIRMED = "calendar.event.status.confirmed";
    public static final String PROPERTY_EVENT_STATUS_TENTATIVE = "calendar.event.status.tentative";
    public static final String PROPERTY_EVENT_STATUS_LIST = "calendar.event.status.list";
    public static final String PROPERTY_EVENT_STATUS_DEFAULT = "calendar.event.status.default";
    public static final String PROPERTY_CONFIRM_UNSUBSCRIPTION_ALERT_MESSAGE = "calendar.siteMessage.unsubscription.message";
    public static final String PROPERTY_CONFIRM_UNSUBSCRIPTION_TITLE_MESSAGE = "calendar.siteMessage.unsubscription.title";

    // Properties for page titles
    public static final String PROPERTY_PAGE_TITLE_MANAGE_EVENTS = "calendar.calendar_manage_events.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_MANAGE_CALENDARS = "calendar.manage_calendars.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_CREATE_CALENDAR = "calendar.create_calendar.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_MODIFY_CALENDAR = "calendar.modify_calendar.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_CREATE_EVENT = "calendar.create_event.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_MODIFY_EVENT = "calendar.modify_event.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_MODIFY_OCCURRENCE = "calendar.modify_occurrence.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_SEARCH_RESULT = "calendar.search.event.result.title";
    public static final String PROPERTY_PAGE_TITLE_SEARCH = "calendar.search.event.title";
    public static final String PROPERTY_PAGE_SUBSCRIPTION_TITLE = "calendar.subscription.event.title";
    public static final String PROPERTY_PAGE_DOWNLOAND_TITLE = "calendar.download.agenda.title";
    public static final String PROPERTY_PAGE_EMAIL_FRIEND_TITLE = "calendar.email-friend.event.title";
    public static final String PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIBERS = "calendar.manage.subscribers.calendar.pageTitle";
    public static final String PROPERTY_CALENDAR_DOTS_PATH = "calendar.dots.path";
    public static final String PROPERTY_MESSAGE_DATEFORMAT = "calendar.message.dateFormat";
    public static final String PROPERTY_MESSAGE_DATE_END_BEFORE = "calendar.message.dateend.before";
    public static final String PROPERTY_MESSAGE_TIMEFORMAT = "calendar.message.timeFormat";
    public static final String PROPERTY_MESSAGE_EXIST = "calendar.message.exist";
    public static final String PROPERTY_MESSAGE_CANNOT_DELETE_ALL_OCC = "calendar.message.occurrence.cannot_delete_all";
    public static final String PROPERTY_EVENTS_PER_PAGE = "calendar.eventsPerPage";
    public static final String PROPERTY_SORT_EVENTS = "calendar.modify_calendar.sortEvents";
    public static final String PROPERTY_PAGE_RSS_TITLE = "calendar.rss.title";
    public static final String PROPERTY_WEBMASTER_EMAIL = "email.webmaster";

    //Parameters for Back Office
    public static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    public static final String PARAMETER_CALENDAR_NAME = "calendar_name";
    public static final String PARAMETER_CALENDAR_IMAGE = "calendar_image";
    public static final String PARAMETER_CALENDAR_PREFIX = "calendar_prefix";
    public static final String PARAMETER_CALENDAR_ROLE = "calendar_role";
    public static final String PARAMETER_EVENT_OLD_DATE = "event_old_date";
    public static final String PARAMETER_EVENT_OLD_TITLE = "event_old_title";
    public static final String PARAMETER_WORKGROUP = "workgroup";
    public static final String PARAMETER_CALENDAR_ROLE_MANAGER = "calendar_role_manager";
    public static final String PARAMETER_PAGE_INDEX = "page_index";
    public static final String PARAMETER_SUBSCRIBER_SEARCH = "subscriber_search";
    public static final String PARAMETER_CALENDAR_NOTIFICATION = "calendar_notification";
    public static final String PARAMETER_CALENDAR_PERIOD = "calendar_period";
    public static final String PARAMETER_DASHBOARD_N_NEXT_DAYS = "dashboard_n_next_days";
    public static final String PARAMETER_DASHBOARD_NB_EVENTS = "dashboard_nb_events";

    //Parameters for front office
    public static final String PARAMETER_ACTION = "action";
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_DATE_START = "date_start";
    public static final String PARAMETER_DATE_END = "date_end";
    public static final String PARAMETER_PERIOD = "period";
    public static final String PARAMETER_SENDER_EMAIL = "sender_email";
    public static final String PARAMETER_SENDER_FRIEND_EMAIL = "sender_friend_email";
    public static final String PARAMETER_SENDER_MESSAGE = "event_description";
    public static final String PARAMETER_SENDER_FIRST_NAME = "sender_first_name";
    public static final String PARAMETER_SENDER_LAST_NAME = "sender_last_name";
    public static final String PARAMETER_MONTH = "month";
    public static final String PARAMETER_NEXT = "next";
    public static final String PARAMETER_PREV = "prev";
    public static final String PARAMETER_PAGE_ID = "page_id";

    //Common parameters for back and front office
    public static final String PARAMETER_EVENT_DATE = "event_date";
    public static final String PARAMETER_EVENT_DATE_END = "event_date_end";
    public static final String PARAMETER_EVENT_TITLE = "event_title";
    public static final String PARAMETER_EVENT_TIME_START = "event_time_start";
    public static final String PARAMETER_EVENT_TIME_END = "event_time_end";
    public static final String PARAMETER_SORT_EVENTS = "sort_events";
    public static final String PARAMETER_CBX_OCCURRENCE = "cbx_occurrence";
    public static final String PARAMETER_CALENDAR_ID = "calendar_id";
    public static final String PARAMETER_CALENDAR_EXPORT = "calendar_export";
    public static final String PARAMETER_EVENT_ID = "event_id";
    public static final String PARAMETER_OCCURRENCE_ID = "occurrence_id";
    public static final String PARAMETER_PERIODICITY = "periodicity";
    public static final String PARAMETER_RADIO_PERIODICITY = "radioPeriodicity";
    public static final String PARAMETER_OCCURRENCE = "occurrence";
    public static final String PARAMETER_REPEATED_DAYS = "number_days";
    public static final String PARAMETER_DESCRIPTION = "event_description";
    public static final String PARAMETER_EVENT_IMAGE = "event_image";
    public static final String PARAMETER_EVENT_TAGS = "event_tags";
    public static final String PARAMETER_EVENT_LOCATION_ADDRESS = "event_location_address";
    public static final String PARAMETER_LOCATION = "event_location";
    public static final String PARAMETER_LOCATION_TOWN = "event_location_town";
    public static final String PARAMETER_LOCATION_ZIP = "event_location_zip";
    public static final String PARAMETER_EVENT_LINK_URL = "event_link_url";
    public static final String PARAMETER_EVENT_DOCUMENT_ID = "event_document_id";
    public static final String PARAMETER_EVENT_PAGE_URL = "event_page_url";
    public static final String PARAMETER_EVENT_TOP_EVENT = "event_top";
    public static final String PARAMETER_EVENT_STATUS = "event_status";
    public static final String PARAMETER_EVENT_MAP_URL = "event_map_url";
    public static final String PARAMETER_EXPORT_EXTENSION = "export_extension";
    public static final String PARAMETER_STYLESHEET_ID = "stylesheet_id";
    public static final String PARAMETER_EMAIL = "email";
    public static final String PARAMETER_DATE = "date";
    public static final String PARAMETER_NOTIFY = "notify";
    public static final String PARAMETER_SUBSCRIBER_ID = "subscriber_id";
    public static final String PARAMETER_INPUT = "input";
    public static final String PARAMETER_INSERT_SERVICE_TYPE = "insert_service_type";
    public static final String PARAMETER_DOCUMENT_ID = "document_id";
    public static final String PARAMETER_CALENDAR_RSS = "calendar_rss";
    public static final String PARAMETER_CATEGORY = "category";
    public static final String PARAMETER_CONFIRMATION_FORM = "confirmation_form";
    public static final String PARAMETER_TYPE_BOX = "box";
    public static final String PARAMETER_SELECTED_TEXT = "selected_text";
    public static final String PARAMETER_REMOVE_OCCURRENCES = "remove_occurrences";
    public static final String PARAMETER_EXCLUDED_DAYS = "excluded_days";

    //Bookmarks for BackOffice
    public static final String MARK_CALENDARS_LIST = "calendar_list";
    public static final String MARK_EVENTS_LIST = "event_list";
    public static final String MARK_OCCURRENCES_LIST = "occurrence_list";
    public static final String MARK_EVENT = "event";
    public static final String MARK_EVENT_DATE_END = "event_date_end";
    public static final String MARK_OCCURRENCE = "occurrence";
    public static final String MARK_DOTS_LIST = "dots_list";
    public static final String MARK_PAGINATOR = "paginator";
    public static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    public static final String MARK_EVENTS_SORT_LIST = "sort_list";
    public static final String MARK_DEFAULT_SORT_EVENT = "default_sort_event";
    public static final String MARK_WORKGROUPS_LIST = "workgroups_list";
    public static final String MARK_ROLES_LIST = "roles_list";
    public static final String MARK_SUBSCRIBERS_LIST = "subscribers_list";
    public static final String MARK_SEARCH_STRING = "search_string";
    public static final String MARK_DOCUMENT = "document";
    public static final String MARK_EXPORT_STYLESHEET_LIST = "stylesheet_list";

    //Bookmarks used in front
    public static final String MARK_PREVIOUS = "previous";
    public static final String MARK_NEXT = "next";
    public static final String MARK_TITLE = "title";
    public static final String MARK_LEGEND = "legend";
    public static final String MARK_VIEW_CALENDAR = "view_calendar";
    public static final String MARK_EVENT_LIST = "event_list";
    public static final String MARK_SMALL_MONTH_CALENDAR = "small_month_calendar";
    public static final String MARK_AGENDA_RESOURCE_LIST = "agenda_resource_list";
    public static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    public static final String MARK_CAPTCHA = "captcha";

    // Common bookmarks for back and front office
    public static final String MARK_DATE = "date";
    public static final String MARK_CALENDAR_ID = "calendar_id";
    public static final String MARK_CALENDAR = "calendar";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_KEY = "key";

    // Constants
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_NULL = null;
    public static final String SPACE = " ";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String ONE = "1";
    public static final String ZERO = "0";
    public static final String COMMA = ",";
    public static final String NULL = "null";
    public static final String AMPERSAND = "&";
    public static final String EQUAL = "=";
    public static final String INTERROGATION_MARK = "?";
    public static final String INDENT = "-";
    public static final String BEAN_CALENDAR_CALENDARSERVICE = "calendar.calendarService";
    public static final String BEAN_CALENDAR_EVENTLISTSERVICE = "calendar.eventListService";
    public static final int SORT_ASC = 1;
    public static final int SORT_DESC = 0;

    // Properties suffix
    public static final String SUFFIX_NAME = ".name";
    public static final String SUFFIX_LOADER_CLASS = ".loader.class";
    public static final String SUFFIX_LOADER_PARAMETER = ".loader.parameter";
    public static final String SUFFIX_LABEL = ".label";
    public static final String SUFFIX_EVENT_IMAGE = ".event.image";
    public static final String SUFFIX_ROLE = ".role";
    public static final String SUFFIX_CLASS = ".class";
    public static final String SUFFIX_TITLE = ".title";

    // Session Attributes
    public static final String ATTRIBUTE_CALENDAR_VIEW = "CALENDAR_CURRENT_VIEW_CLASS";
    public static final String ATTRIBUTE_CALENDAR_AGENDA = "CALENDAR_CURRENT_AGENDA";
    public static final String ATTRIBUTE_CALENDAR_AGENDA_OCCURRENCES = "CALENDAR_CURRENT_AGENDA_OCCURENCES";

    // Views
    public static final String VIEW_DAY = "day";
    public static final String VIEW_WEEK = "week";
    public static final String VIEW_MONTH = "month";

    // Actions
    public static final String ACTION_MANAGE_EVENTS = "manage_events";
    public static final String ACTION_ADD_EVENT = "add_event";
    public static final String ACTION_MODIFY_EVENT = "modify_event";
    public static final String ACTION_REMOVE_EVENT = "remove_event";
    public static final String ACTION_DO_CREATE_EVENT = "do_create_event";
    public static final String ACTION_DO_MODIFY_EVENT = "do_modify_event";
    public static final String ACTION_DO_REMOVE_EVENT = "do_remove_event";
    public static final String ACTION_GET_SUBSCRIPTION_PAGE = "get_subscribe_page";
    public static final String ACTION_GET_FRIEND_EMAIL_PAGE = "get_friend_email_page";
    public static final String ACTION_GET_DOWNLOAD_PAGE = "get_download_page";
    public static final String ACTION_SEND_FRIEND_EMAIL = "send_friend";
    public static final String ACTION_VERIFY_SUBSCRIBE = "verify_subscribe";
    public static final String ACTION_CONFIRM_UNSUBSCRIBE = "confirm_unsubscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    public static final String ACTION_SEARCH = "search";
    public static final String ACTION_DO_SEARCH = "do_search";
    public static final String ACTION_SHOW_RESULT = "show_result";
    public static final String ACTION_RSS = "rss";

    // CSS Styles
    public static final String STYLE_CLASS_VIEW_MONTH_DAY = AppPropertiesService
            .getProperty( "calendar.style.month.day" );
    public static final String STYLE_CLASS_VIEW_WEEK_DAY = AppPropertiesService.getProperty( "calendar.style.week.day" );
    public static final String STYLE_CLASS_SMALLMONTH_DAY = AppPropertiesService
            .getProperty( "calendar.style.smallmonth.day" );
    public static final String STYLE_CLASS_SUFFIX_OLD = AppPropertiesService.getProperty( "calendar.style.suffix.old" );
    public static final String STYLE_CLASS_SUFFIX_TODAY = AppPropertiesService
            .getProperty( "calendar.style.suffix.today" );
    public static final String STYLE_CLASS_SUFFIX_OFF = AppPropertiesService.getProperty( "calendar.style.suffix.off" );
    public static final String STYLE_CLASS_SUFFIX_EVENT = AppPropertiesService
            .getProperty( "calendar.style.suffix.event" );
    public static final String STYLE_CLASS_SELECTED_DAY = AppPropertiesService
            .getProperty( "calendar.style.selected.day" );

    // Indexer fileds
    public static final String FIELD_CALENDAR_ID = "calendar_id";
    public static final String FIELD_CATEGORY = "category";
    public static final String CALENDAR_SHORT_NAME = "cld";
    public static final String DOCUMENT_SHORT_NAME = "dcld";

    // Regex
    public static final String REG_NUMBER = "^[0-9]+$";

    /**
     * Default constructor
     */
    private Constants( )
    {

    }
}
