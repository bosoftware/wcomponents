<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	version="1.0">

	<xsl:import href="wc.common.registrationScripts.xsl"/>

	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
		ui:ajaxTarget is a child of ui:ajaxResponse (wc.ui.ajaxResponse.xsl).

		The main point of this template is a simple pass-through to output the contained
		elements. The order of application is important here. We have to apply all
		templates then build any included dialogs and then run the registration scripts
		to wire up new onload functionality.
	-->
	<xsl:template match="ui:ajaxTarget">
		<xsl:apply-templates />
		<xsl:call-template name="registrationScripts" />
	</xsl:template>

	<!--
		This mode is invoked in the faux-ajax used to do inline multi file uploads. It
		is only required to pass through to output the contained elements. You
		may want to take a look at wc.ui.fileUpload.xsl and wc.ui.fileUpload.js
	-->
	<xsl:template match="ui:ajaxTarget" mode="pseudoAjax">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>