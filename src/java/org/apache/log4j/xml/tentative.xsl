<?xml version="1.0"?>

<!-- This XSL stylesheet is a get-to-know experiment with the XSL
language. Future log4j versions might include different perhaps more
useful stylesheets. 



-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:log4j="http://log4j.org"
>

<xsl:variable name="start">
   <xsl:value-of select="/log4j:eventSet/log4j:event/@timestamp"/>
</xsl:variable>

<xsl:variable name="includesLocationInfo">
   <xsl:value-of select="/log4j:eventSet/@includesLocationInfo"/>
</xsl:variable>

<xsl:variable name="relativeTime">
   <xsl:value-of select="/log4j:eventSet/@relativeTime"/>
</xsl:variable>


<xsl:template match="/">
  <html>
    <head></head>
    <body bgcolor="white">
      <xsl:apply-templates select="log4j:eventSet"/>
    </body>
    </html>  

</xsl:template>


<xsl:template match="log4j:eventSet">
   <table border="1" cellspacing="2" cellspadding="2">
      <tr>
         <xsl:choose>
            <xsl:when test="$relativeTime='true'">
               <th>Rel. Time</th>         
            </xsl:when>
            <xsl:otherwise>
               <th>Time</th>   
            </xsl:otherwise>         
         </xsl:choose>

         <th>Priority</th>
         <th>Thread</th>
         <th>Category</th>
         <th>NDC</th>
         <xsl:if test="$includesLocationInfo='true'">
            <th>File:Line</th>
            <th>Method</th>
         </xsl:if>
         <th>Message</th>
       </tr>       
       <xsl:apply-templates select="log4j:event"/>
   </table>
</xsl:template>


<xsl:template match="log4j:event">
   <tr valign="top">
      <xsl:choose>
         <xsl:when test="$relativeTime='true'">
            <td><xsl:value-of select="(@timestamp)-($start)"/></td> 
        </xsl:when> 
        <xsl:otherwise>
            <td><xsl:value-of select="@timestamp"/></td>
        </xsl:otherwise>
      </xsl:choose> 
      <td><font>
         <xsl:choose>
            <xsl:when test="self::node()[@priority='ERROR' or
                                         @priority='EMERG']"> 
              <xsl:attribute name="color">#FF0000</xsl:attribute>   
            </xsl:when>
            <xsl:when test="self::node()[@priority='WARN']"> 
              <xsl:attribute name="color">#FF6600</xsl:attribute>   
            </xsl:when>
            <xsl:otherwise>          
               
            </xsl:otherwise>
         </xsl:choose> 
         <xsl:value-of select="./@priority"/>
      </font></td>

    <td><xsl:value-of select="./@thread"/></td>
    <td><xsl:value-of select="./@category"/></td>
    <td><xsl:value-of select="log4j:NDC"/></td>


    <xsl:if test="$includesLocationInfo='true'">
       <td>
          <xsl:value-of select="child::log4j:locationInfo/@file"/>
          :
          <xsl:value-of select="child::log4j:locationInfo/@line"/>
       </td>
       <td>
          <xsl:value-of select="child::log4j:locationInfo/@method"/>
       </td>
    </xsl:if>

    <td><xsl:value-of select="log4j:message"/></td>    
    
  </tr>
  <xsl:apply-templates select="log4j:throwable"/>


</xsl:template>

<xsl:template match="log4j:throwable">
 <tr>
   <td colspan="8"><pre><xsl:value-of select="."/></pre></td>
 </tr>
</xsl:template>
 

</xsl:stylesheet>