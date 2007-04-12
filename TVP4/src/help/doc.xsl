<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	
	<xsl:template match="/">
	  <html>
	  <head>
	  <style>
		<![CDATA[
		
			BODY {			
				font-family: Verdana;
				font-size: 11px;
			}	
			
			LI {
				margin: 0px 0px 4px 0px;
			}
					
			.topicName {
				font-weight: bold;
				font-size: 16px;
			}
			
			.menuItem {
				font-weight: bold;
				font-size: 14px;
				background: url(bull.gif) no-repeat; 
				margin: 0px 0px 3px 12px;
			}
							
			.question {
				font-weight: bold;
				margin: 10px 0px 1px 5px;
			}
			
			.answer {			
				margin-left: 10px;
			}
			
		]]>
	  </style>
	  </head>
	  <body>
	  	  <xsl:apply-templates/>		  
	  </body>
	  </html>
	</xsl:template>
	
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