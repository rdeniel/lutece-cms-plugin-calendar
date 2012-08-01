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
	
		<div class="append-bottom  {$device_class}">
			<xsl:choose>
				<xsl:when test="not(string(display-portlet-title)='1')">
					<h3 class="portlet-background-header -lutece-border-radius-top">
						<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
					</h3>
					<div class="portlet-background-content -lutece-border-radius-bottom" >
						<xsl:apply-templates select="calendar-filtered-list" />
						<xsl:text disable-output-escaping="yes">
							<![CDATA[<div class="clear">&#160;</div>]]>
						</xsl:text>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="portlet-background-content -lutece-border-radius" >
						<xsl:apply-templates select="calendar-filtered-list" />
						<xsl:text disable-output-escaping="yes">
							<![CDATA[<div class="clear">&#160;</div>]]>
						</xsl:text>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>        
	</xsl:template>

	<xsl:template match="calendar-filtered-list">
		<xsl:apply-templates select="events" />
	</xsl:template>

	<xsl:template match="events">    
		<xsl:apply-templates select="event" /> 
	</xsl:template>

	<xsl:template match="event">
		[
        <xsl:value-of select="date-local" /> 
        ]
		<a href="{$site-path}?page=calendar&#38;agenda={agenda-id}&#38;date={event-date}" target="_top" title=" {agenda-name}:{event-title} ">          
			<xsl:value-of select="event-title" /> 
		</a>
		<br />
	</xsl:template>
</xsl:stylesheet>
