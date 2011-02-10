<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" 
indent="no"/>
<xsl:variable name="header">
BEGIN:VCALENDAR
VERSION:2.0
X-WR-CALNAME:Agenda
PRODID:-//Mairie de Paris Inc//iCal 2.0//EN
CALSCALE:GREGORIAN
</xsl:variable>
<xsl:variable name="footer">
END:VCALENDA
</xsl:variable>
<xsl:template match="events">		
<xsl:copy-of select="$header" />
<xsl:apply-templates select="event" />
<xsl:copy-of select="$footer" />
</xsl:template >
<xsl:template match="event">
BEGIN:VEVENT	
SUMMARY:<xsl:value-of select="event-summary" />
LOCATION:<xsl:value-of select="event-location" />
DESCRIPTION:<xsl:value-of select="event-description" />
CATEGORIES:<xsl:value-of select="event-categories" />
STATUS:<xsl:value-of select="event-status" />
DTSTART;TZID=Europe/Paris:<xsl:value-of select="event-date" /><xsl:if test="event-date-time-start != '' ">T<xsl:value-of select="event-date-time-start" />00</xsl:if>
<xsl:if test="event-date-time-end != '' ">DTEND;TZID=Europe/Paris:<xsl:value-of select="event-date" />T<xsl:value-of select="event-date-time-end" />00</xsl:if>
END:VEVENT        	
</xsl:template>
</xsl:stylesheet>