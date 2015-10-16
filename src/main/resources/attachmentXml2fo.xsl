<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="my-page">
					<fo:region-body margin="1cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="my-page">
				<fo:flow flow-name="xsl-region-body">
					<xsl:if test="data/attachment != ''">
						<fo:block>
							<fo:external-graphic content-height="scale-to-fit"
								max-width="90%"
								max-height="90%">
								<xsl:attribute name="src">
									<xsl:text>url('data:image/png;base64,</xsl:text>
									<xsl:value-of select="data/attachment/." />
									<xsl:text>')</xsl:text>
								</xsl:attribute>
							</fo:external-graphic>
						</fo:block>
					</xsl:if>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>