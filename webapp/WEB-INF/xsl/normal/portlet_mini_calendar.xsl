<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:variable name="url"><xsl:value-of select="portlet/url"/></xsl:variable>
    <xsl:variable name="page-id"><xsl:value-of select="portlet/page-id"/></xsl:variable>
	<xsl:template match="portlet">
		<xsl:variable name="device_class">
			<xsl:choose>
				<xsl:when test="string(display-on-small-device)='0'">hidden-phone</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<div class="portlet {$device_class}">
			<div class="span4">
				<xsl:apply-templates select="month" />
				<xsl:apply-templates select="events" />
			</div>
			<div class="span7">
				<xsl:apply-templates select="top-events" />&#160;
			</div>
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
			<a href="jsp/site/Portal.jsp?page_id={$page-id}&amp;date={week-shortcut-date-start}&amp;date_end={week-shortcut-date-end}">
				<i class="icon-calendar">&#160;</i>&#160;<xsl:value-of select="week-shortcut-label" disable-output-escaping="yes" />
			</a>				
			<br/>
	</xsl:template>

	<xsl:template match="top-events">	
	<div id="top-event" class="galleryview">
		<xsl:apply-templates select="top-event"/>
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
		<xsl:apply-templates select="event" /> 
	</xsl:template>
	
	<xsl:template match="event">
		[<xsl:value-of select="date" />
		<xsl:if test="date-end != ''">
			-&#160;<xsl:value-of select="date-end" />
		</xsl:if>]
		<xsl:if test="not(event-datetime-begin='')">
		-
		<xsl:value-of select="event-datetime-begin" /> :
		</xsl:if> 
		<a href="jsp/site/Portal.jsp?page=calendar&amp;action=show_result&amp;event_id={event-id}" target="_blank">
		 	<xsl:value-of select="event-title" /> 
		</a>
	</xsl:template>
	
</xsl:stylesheet>
