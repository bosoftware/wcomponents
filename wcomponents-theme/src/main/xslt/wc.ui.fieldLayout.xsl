<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- WFieldLayout -->
	<xsl:template match="ui:fieldlayout">
		<div role="presentation"><!-- yes, I know the role is superfluous -->
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="@labelWidth">
						<xsl:value-of select="concat('wc_fld_lblwth_',@labelWidth)"/>
					</xsl:if>
					<xsl:if test="@ordered">
						<xsl:text> wc_ordered</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@ordered and number(@ordered) ne 1">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('counter-reset: wcfld ', number(@ordered) - 1, ';')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:field"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
