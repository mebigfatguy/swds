<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                             xmlns:d="DAV:">

	<xsl:param name="lockType"/>
	<xsl:param name="lockScope"/>
	<xsl:param name="lockDepth"/>
	<xsl:param name="lockOwner"/>
	<xsl:param name="lockTimeout"/>
	<xsl:param name="lockToken"/>
	
	<xsl:template match="@*|node()">
	  <xsl:copy>
	    <xsl:apply-templates select="@*|node()"/>
	  </xsl:copy>
	</xsl:template>
	
	<xsl:template match="d:locktype">
		<xsl:copy>
			<xsl:element name="d:{$lockType}" namespace="DAV:"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="d:lockscope">
		<xsl:copy>
			<xsl:element name="d:{$lockScope}" namespace="DAV:"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="d:deptch">
		<xsl:copy>
			<xsl:value-of select="$lockDepth}"/>
		</xsl:copy>
	</xsl:template>
	
</xsl:transform>