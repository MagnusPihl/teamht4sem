<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	
	<xsl:template match="/">
	  <html>
	  <head>
	  <title>HT++ - Help</title>
	  <script language="javascript" type="text/javascript">
		<!-- [CDATA[
			function topicClick(obj) {
				document.getElementById("dataCell").innerHTML = "<div class='topicName'>" +obj.childNodes[0].innerHTML + "</div>"+ obj.childNodes[1].innerHTML;
			}
		]]-->
	  </script>
	  <style>
		<![CDATA[
		
			BODY {			
				font-family: Verdana;
				font-size: 11px;
			}	
			
			IMG {
				padding: 5px;
			}
			
			<!-- A, A:hover, A:link, A:active, A:visited {
				text-decoration: none;
				font-color: #FFFFFF;
			}	-->
			
			LI {
				padding: 0px 0px 4px 0px;
			}
			
			.topic {
				cursor: pointer;
			}
			
			.topicName {
				font-weight: bold;
				font-size: 16px;
			}
			
			.menuItem {
				font-weight: bold;
				font-size: 15px;
				width: 740px;
				border-bottom: 1px solid #8DEEF5;
			}		
			
			.dataCell {
				padding: 10px;
				padding-top:16px;
				width:740px;
				float:left;
				border-bottom: 1px solid #8DEEF5;
			}
			
			.mainCell {
				width: 760px;
				height: 100%;
				text-align: left;	
				left: 200px;		
				position: absolute;
			}
			
			.question {
				font-size: 12px;
				font-weight: bold;
				padding: 10px 0px 3px 5px;
			}
			
			.answer {			
				padding-left: 10px;
				padding-bottom: 5px;
				line-height: 18px;
			}
			
			.topLogo {
				width: 760px;
				height: 100px;
				background: url(ht_top.png);
			}
			
			.topBar {
				width: 752px;
				padding: 4px;
				background: #80FFFF;
				border-top: 1px solid #8DEEF5;
			}
			.returnToTop {
				width: 740px;
				padding: 2px;
				position: absolute;
				text-align: right;
				font-weight: normal;
				font-size: 10px;
			}
			.mapCell {
				width: 183px;
				height: 100%;
				padding: 4px;
				position: fixed;
				text-align: left;
				border-right: 1px solid #8DEEF5;
				border-top: 1px solid #8DEEF5;
			}
			.mapTopic {
				padding: 0px 0px 3px 0px;
				font-weight: bold;
				font-size: 13px;
			}			
			.mapQuestion {
				margin: 0px 0px 3px 6px;
				font-size: 11px;
				font-weight: normal;
			}			
		]]>
	  </style>
	  </head>
	  <body><!--onload="top.update(document.getElementById('mainCell').innerHTML);"-->
		  <div class="mainCell" id="mainCell">
			  <div class="topLogo"></div> 	
			  <div class="topBar">&#x00A0;</div>			  
			  <div id="dataCell" class="dataCell">
				  <xsl:apply-templates/>
			  </div>	  
		  </div>
		  
	  	<div class="mapCell">	
			<xsl:for-each select="/topics/topic">	
				<a class="mapTopic" style="color:black;">
					<xsl:attribute name="href">#<xsl:value-of select="generate-id(@name)"/></xsl:attribute>
					<xsl:attribute name="alt">Gå til emnet: <xsl:value-of select="@name"/></xsl:attribute>
					<xsl:value-of select="@name"/>	<br />							
				</a>
				
				<xsl:for-each select="./question">					
					<a class="mapQuestion" style="color:black;">
						<xsl:attribute name="href">#<xsl:value-of select="generate-id(.)"/></xsl:attribute>
						<xsl:attribute name="alt">Gå til emnet: <xsl:value-of select="./name"/></xsl:attribute>
						<xsl:value-of select="./name"/><br />
					</a>
				</xsl:for-each>
				<br />
			</xsl:for-each>
		  </div>	 		  
	  </body>
	  </html>
	</xsl:template>
	
	<xsl:template match="topic">
		<div class="topic"><!-- onclick="topicClick(this);" -->
			<xsl:attribute name="id"><xsl:value-of select="generate-id(@name)"/></xsl:attribute>
			<!--div class="returnToTop"><a href="#mainCell">return to top</a></div-->
			<div class="menuItem">
				<xsl:value-of select="@name"/>
			</div>
			<xsl:apply-templates />
			<br /><br />
		</div>
	</xsl:template>
	
	<xsl:template match="question">
		<div class="question">
			<xsl:attribute name="id"><xsl:value-of select="generate-id(.)"/></xsl:attribute>
			<div class="returnToTop"><a href="#mainCell" style="color:#666666;">Return to the top</a></div>
			<xsl:value-of select="name"/>
		</div>
		<div class="answer"><xsl:copy-of select="answer"/></div>	
		<br />
	</xsl:template>

</xsl:stylesheet>