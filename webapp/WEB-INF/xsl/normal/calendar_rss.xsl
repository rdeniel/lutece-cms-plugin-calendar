<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
	<xsl:template match="events">
		<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
	    <channel>
	        <title>Calendar RSS</title>
	        <description>Calendar RSS</description>
	        <link>portal.jsp</link>	
		<xsl:apply-templates select="event" />
	    </channel>
		</rss>	    
		</xsl:template>
	
		<xsl:template match="event">
	        <item>
	            <title><xsl:value-of select="event-summary" disable-output-escaping="yes" /></title>
	            <description><xsl:value-of select="event-description" disable-output-escaping="yes" /></description>
	            <pubDate><xsl:value-of select="event-creation-date" disable-output-escaping="yes" /></pubDate>
	            <link><xsl:value-of select="event-url"  /></link>
	        </item>
		</xsl:template>
</xsl:stylesheet>
