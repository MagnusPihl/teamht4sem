<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
  <html>
  <head>
  <title>HT++ - Help</title>
  <script language="javascript" type="text/javascript">
  	<![CDATA[
		function topicClick(obj) {
			document.getElementById("dataCell").innerHTML = "<div class='topicName'>" +obj.childNodes[1].innerHTML + "</div>"+ obj.childNodes[2].innerHTML;
		}
	]]>
  </script>
  <style>
  	<![CDATA[
	
		BODY {			
			font-family: Verdana;
			font-size: 11px;
			text-align:center;
		}		
		
		.topic {
			float:left;
			width: 150px;
			cursor: pointer;
		}
		
		.topicName {
			font-weight: bold;
			font-size: 16px;
		}
		
		.menuItem {
			font-weight: bold;
			float: left;
			padding: 0px 0px 3px 0px;
		}
		
		.menuPoint {
			font-weight: bold;
			float: left;
			padding: 0px 4px 3px 0px;
		}
	
		.menuData {
			position:absolute;
			visibility:hidden;
		}
		
		.menuCell {
			padding: 4px;
			width: 150px;
			height: 100%;
			float:left;
			background-color:#FFFF00;
			border: 1px dashed #555500;
		}
		
		.dataCell {
			padding: 83px 0px 0px 10px;
			width:582px;
			height: 100%;
			float:left;
		}
		
		.mainCell {
			width: 760px;
			height: 100%;
			text-align:left;			
		}
		
		.question {
			font-weight: bold;
			padding: 10px 0px 1px 5px;
		}
		
		.answer {			
			padding-left: 10px;
		}
	]]>
  </style>
  </head>
  <body>
  	  <div class="mainCell">
		  <div id="menuCell" class="menuCell">
			  <img src="ht_logo.gif" width="150" height="75"  alt="HT++"/>
			  <xsl:for-each select="topics/topic">
				 <div class="topic" onclick="topicClick(this);">
					<!--<div class="ids" name="id"><xsl:number value="position()" format="1"/></div>-->
					<div class="menuPoint"> - </div>
					<div class="menuItem">
						<xsl:value-of select="name"/>
					</div>
					<div class="menuData">
						<xsl:for-each select="question">
							<div class="question"><xsl:value-of select="name"/></div>
							<div class="answer"><xsl:value-of select="answer"/></div>		
						</xsl:for-each>
					</div>
				</div>
			 </xsl:for-each>
		  </div>
		  <div id="dataCell" class="dataCell">dsadsa</div>	  
	  </div>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>