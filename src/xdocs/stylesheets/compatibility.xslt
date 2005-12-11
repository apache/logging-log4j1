<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  
  <xsl:template match="/">
     <html>
        <head>
           <title>Apache log4j compatibility report</title>
        </head>
        <body>
            <h2>Apache log4j compatibility report</h2>
        	<xsl:apply-templates select="*"/>
        </body>
      </html>
  </xsl:template>
  
  <xsl:template match="diffreport">
     <table border="1" summary="Source and binary compatibility errors">
     	<xsl:apply-templates select="difference[@srcseverity='ERROR' or @binseverity='ERROR']"/>
	 </table>
  </xsl:template>
  
  <xsl:template match="difference">
  	  <tr>
  	     <td>
      	    <xsl:choose>
     		   <xsl:when test="contains(@class,'org.apache.log4j.')">o.a.l.<xsl:value-of select="substring-after(@class, 'org.apache.log4j.')"/></xsl:when>
     		   <xsl:otherwise><xsl:value-of select="@class"/></xsl:otherwise>
     	    </xsl:choose>
     	    <xsl:if test="@method">
     	    	<xsl:text>.</xsl:text>
     	    	<xsl:call-template name="last-word">
     	    		<xsl:with-param name="phrase" select="substring-before(@method, '(')"/>
     	    	</xsl:call-template>
     	    </xsl:if>
     	 </td>
  	     <td><xsl:value-of select="text()"/></td>
  	  </tr>
  </xsl:template>
  
  <xsl:template name="last-word">
  	<xsl:param name="phrase"/>
  	<xsl:choose>
  		<xsl:when test="contains($phrase, ' ')">
  			<xsl:call-template name="last-word">
  				<xsl:with-param name="phrase" select="substring-after($phrase, ' ')"/>
  			</xsl:call-template>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:value-of select="$phrase"/>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
  <xsl:template match="@class">
     	<xsl:choose>
     		<xsl:when test="contains(.,'org.apache.log4j.')">o.a.l.<xsl:value-of select="substring-after(., 'org.apache.log4j.')"/></xsl:when>
     		<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
     	</xsl:choose>
  </xsl:template>
</xsl:transform>