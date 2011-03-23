<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<head>
				<link href="style.css" rel="stylesheet" type="text/css" />
				<script src="jquery.js" type="text/javascript"></script>
				<script type="text/javascript">
					$(document).ready(function(){
						var active="none";
						$("tr.to_hide").hide();
						
						$("td.name").click(function(){
							var tmp_id= $(this).attr("id");
							$("tr.to_hide").hide();
							if(active!=tmp_id){
								active = tmp_id;
								$(this).parent().parent().children().show();
							}else{
								active = "none";
							}
						});
					});
				</script>
			</head>
			<body>
				<xsl:for-each select="/cameras">
					<h2><xsl:value-of select="@brand"/></h2>
					<xsl:call-template name="printCam"/>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="printCam">  
			<xsl:for-each select="camera">
			<table border="1" cellpadding="5">
			<tr>
				<td class="name left">
					<xsl:attribute name="id"><xsl:value-of select="name"/></xsl:attribute>
						Name
				</td> 
				<td class="name">
					<xsl:attribute name="id"><xsl:value-of select="name"/></xsl:attribute>	
					<xsl:value-of select="name"/>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class"><xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Description</td> 
				<td><xsl:value-of select="description"/></td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>
				<td class="left">Sumary</td>
				<td><xsl:value-of select="sumary"/></td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Release Date</td> 
				<td><xsl:value-of select="releasedDate"/></td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Maximum Resolution</td> 
				<td>
					<xsl:for-each select="resolutions/maxResolution">
						<xsl:value-of select="@width"/>x<xsl:value-of select="@height"/><br/>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Lower Resolutions</td> 
				<td>
					<xsl:for-each select="resolutions/lowResolutions/resolution">
                        <xsl:value-of select="@width"/>x<xsl:value-of select="@height"/><br/>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Image Ratio</td> 
				<td>
					<xsl:for-each select="imageRatios/imageRatio">
						<xsl:value-of select="@width"/>x<xsl:value-of select="@height"/><br/>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Effective pixels</td> 
				<td><xsl:value-of select="effectivePixels"/></td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Sensor size</td> 
				<td><xsl:for-each select="sensorSize">
					<xsl:value-of select="@width"/>x<xsl:value-of select="@height"/><br/>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Iso Rating</td> 
				<td>
					<xsl:for-each select="ISOratings/ISOrating">
						<xsl:if test="string-length(text()) != 0">
							<xsl:value-of select="."/><br/>
						</xsl:if>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Shutter speed</td> 
				<td>
					<xsl:for-each select="shutterTime">
						Min:<xsl:value-of select="@min"/><br/>
						Max:<xsl:value-of select="@max"/><br/>
					</xsl:for-each>
				</td>
			</tr>
			<tr>
				<xsl:attribute name="class">to_hide <xsl:value-of select="name"/>_all</xsl:attribute>	
				<td class="left">Links</td> 
				<td>
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="links/inDepthReview"/>
						</xsl:attribute>
						<xsl:value-of select="links/inDepthReview"/>
					</a><br/>
					<img><xsl:attribute name="src"><xsl:value-of select="links/picture"/></xsl:attribute></img>
				</td>
			</tr>
			</table>
			</xsl:for-each>
			<br/>
	</xsl:template>
	
</xsl:stylesheet>