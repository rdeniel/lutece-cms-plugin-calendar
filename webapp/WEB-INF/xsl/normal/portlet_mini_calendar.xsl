<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:variable name="url"><xsl:value-of select="portlet/url"/></xsl:variable>
    <xsl:variable name="page-id"><xsl:value-of select="portlet/page-id"/></xsl:variable>
	<xsl:template match="portlet">
		<link rel="stylesheet"  href="css/plugins/calendar/calendar.css" type="text/css"  media="screen" />
		
		<xsl:apply-templates select="top-events" />
		<div class="portlet-background-content -lutece-border-radius-bottom span-6 calendar-mini">
			<xsl:apply-templates select="month" />
			<xsl:text disable-output-escaping="yes">
				</xsl:text>
			<xsl:apply-templates select="events" />
		</div>   					
	</xsl:template>

	<xsl:template match="month">
	    <table class="calendar-smallmonth" cellspacing="0">
        <tr>
            <th class="calendar-smallmonth" colspan="7">
            	<a class="left" href="{$url}{$page-id}&amp;month=prev">&lt;&lt;</a>
				<xsl:value-of select="month-label" disable-output-escaping="yes" />
				<a class="right" href="{$url}{$page-id}&amp;month=next">&gt;&gt;</a>
            </th>
        </tr>
		<xsl:apply-templates select="weeks"/>				
		</table>
		<xsl:apply-templates select="week-shortcuts" />
	</xsl:template>

	<xsl:template match="weeks"> 
		<tr>
			<xsl:apply-templates select="day-label" />
		</tr>	
		
		<xsl:apply-templates select="week" />
		
	</xsl:template>
	
	<xsl:template match="day-label">
			<th class="calendar-smallmonth"><xsl:apply-templates /></th>
	</xsl:template> 
	
	<xsl:template match="week">    
		<tr>
			<xsl:apply-templates select="day"/> 
		</tr>
	</xsl:template>

	<xsl:template match="day">
		<xsl:value-of select="day-code" disable-output-escaping="yes" /> 
	</xsl:template>
	
	<xsl:template match="week-shortcuts">
		<xsl:apply-templates select="week-shortcut"/> 
	</xsl:template>
	
	<xsl:template match="week-shortcut">	
			<img height="16" border="0"  width="16" title="Agenda" alt="Agenda" src="images/local/skin/plugins/calendar/mini_calendar.png"/>		
				<xsl:text disable-output-escaping="yes">
						<![CDATA[&nbsp;]]>
				</xsl:text>
			<a href="jsp/site/Portal.jsp?date={week-shortcut-date-start}">
			<xsl:value-of select="week-shortcut-label" disable-output-escaping="yes" /></a>				
			 
		<br/>
	</xsl:template>

	<xsl:template match="top-events">	
	<div class="span-15" >
	<div id="top-event" class="galleryview">
		<xsl:apply-templates select="top-event"/>
	</div>
	</div>
	</xsl:template>
	<xsl:template match="top-event">
		<div class="panel">
			<img src="image?resource_type=image_event&amp;id={event-id}" /> 
			<div class="panel-overlay">
				<h4><a href="jsp/site/Portal.jsp?page=calendar&amp;action=show_result&amp;event_id={event-id}&amp;agenda={agenda-id}" target="_blank"><xsl:value-of select="top-event-title" disable-output-escaping="yes" /></a></h4>				
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="events">
		<br/>
		[<xsl:value-of select="date" /> ]
		<xsl:apply-templates select="event" /> 
	</xsl:template>
	
	<xsl:template match="event">
	<div>
		<span class = "calendar-event-list-header">
			<xsl:value-of select="event-title" /> 
		</span>
		<xsl:if test="not(event-datetime-begin='')">
			-
			<xsl:value-of select="event-datetime-begin" /> 
		</xsl:if> 
		:
		<a href="jsp/site/Portal.jsp?page=calendar&amp;action=show_result&amp;event_id={event-id}" target="_blank">
		 	<xsl:value-of select="event-description" /> 
		</a>
	</div>
	</xsl:template>
	
</xsl:stylesheet>
