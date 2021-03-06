<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.listSortControls.xsl"/>
	<!-- WShuffler is a component designed to allow a fixed list of options to have their order changed. -->
	<xsl:template match="ui:shuffler">
		<xsl:variable name="id" select="@id"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<ol>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="isWrapper" select="1"/>
						<xsl:with-param name="class">
							<xsl:text>wc_list_nb wc-ro-input</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="title"/>
					<xsl:call-template name="roComponentName"/>
					<xsl:apply-templates select="ui:option|ui:optgroup" mode="readOnly">
						<xsl:with-param name="single" select="0"/>
					</xsl:apply-templates>
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonInputWrapperAttributes"/>
					<xsl:variable name="listId" select="concat($id,'_input')"/>
					<select id="{$listId}" class="wc_shuffler wc-noajax" multiple="multiple" autocomplete="off">
						<xsl:call-template name="disabledElement"/>
						<xsl:if test="number(@rows) gt 2">
							<xsl:attribute name="size">
								<xsl:value-of select="@rows"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates mode="selectableList"/>
					</select>
					<xsl:call-template name="listSortControls">
						<xsl:with-param name="id" select="$listId"/>
					</xsl:call-template>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
