<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
	
	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hide-for-small</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>

		<div class="{$device_class} append-bottom" >
			<div class="portlet-background-content -lutece-border-radius-bottom span-6 calendar-mini calendar-search-float-left">
				<xsl:apply-templates select="month" />
				<xsl:text disable-output-escaping="yes">
					</xsl:text>
			</div> 
			<div class="portlet-background-content -lutece-border-radius calendar-search-float-left span-13" >
				<xsl:apply-templates select="calendar-filtered-list" />
				<xsl:text disable-output-escaping="yes">
					<![CDATA[<div class="clear">&#160;</div>]]>
				</xsl:text>
			</div>
		</div>
		<div class="calendar-clearboth"></div>        
	</xsl:template>

	<xsl:template match="month">
	    <table class="calendar-smallmonth" cellspacing="0">
        <tr>
            <th class="calendar-smallmonth" colspan="7">
				<xsl:value-of select="month-label" disable-output-escaping="yes" />
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
			<a href="jsp/site/Portal.jsp?page=calendar&amp;action=do_search&amp;date_start={week-shortcut-date-start}&amp;date_end={week-shortcut-date-end}&amp;period={week-shortcut-period}">
			<xsl:value-of select="week-shortcut-label" disable-output-escaping="yes" /></a>				
			 
		<br/>
	</xsl:template>
	
	<xsl:template match="calendar-filtered-list">
		<xsl:apply-templates select="events" />
	</xsl:template>

	<xsl:template match="events">    
		<xsl:apply-templates select="event" /> 
	</xsl:template>

	<xsl:template match="event">	
		<p class="calendar-organized-event">
	        <a href="jsp/site/Portal.jsp?page=calendar&amp;action=show_result&amp;event_id={event-id}&amp;agenda={agenda-id}" target="_blank">
	        	<xsl:value-of select="event-title" disable-output-escaping="yes" />
	        </a>
        </p>

	</xsl:template>
</xsl:stylesheet>
