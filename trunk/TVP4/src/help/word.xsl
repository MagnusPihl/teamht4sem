<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml">	
	<xsl:template match="/">
		<xsl:processing-instruction name="mso-application">
			<xsl:text>progid="Word.Document"</xsl:text>
		</xsl:processing-instruction>
		<w:wordDocument>
			<xsl:attribute name="xml:space">preserve</xsl:attribute>
			<w:body>
				<xsl:apply-templates>
	        </w:body>
	    </w:wordDocument>
	</xsl:apply-templates>
	
	<xsl:template match="topic">
		<div class="topicName">
			<xsl:value-of select="@name"/>
		</div>
		<xsl:apply-templates/>
		<br /><br />		
	</xsl:template>
	
	<xsl:template match="question">
		<div class="question"><xsl:value-of select="name"/></div>
		<div class="answer"><xsl:copy-of select="answer"/></div>
		<br/>
	</xsl:template>

</xsl:stylesheet>