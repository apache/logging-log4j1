<!--
  Copyright 2002, 2005 The Apache Software Foundation.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  
-->
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
     	<xsl:apply-templates select="difference[(@srcseverity='ERROR' or @binseverity='ERROR') and not(starts-with(@class, 'org.apache.log4j.lf5'))]"/>
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