<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" indent="yes" />

	<xsl:attribute-set name="title">
		<xsl:attribute name="font-size">24pt</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="text">
		<xsl:attribute name="font-size">12pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="table-row">
		<xsl:attribute name="margin-top">5mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tableProperties">
		<xsl:attribute name="border-before-width.conditionality">retain</xsl:attribute>
		<xsl:attribute name="border-collapse">collapse</xsl:attribute>
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
		<xsl:attribute name="border-top">solid 0.2mm black</xsl:attribute>
		<xsl:attribute name="border-bottom">solid 0.2mm black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:template match="text()[contains(.,'T') and contains(.,'Z')]"
		name="dateFilter">
		<xsl:param name="dd" select="." />
		<xsl:variable name="a" select="substring-before($dd,'T')" />
		<xsl:variable name="l" select="string-length($a)" />
		<xsl:variable name="d" select="substring($a,$l - 1,2)" />
		<xsl:variable name="m" select="substring($a,$l -4,2)" />
		<xsl:variable name="y" select="substring($a,$l -9,4)" />
		<xsl:value-of select="concat($d,'.',$m,'.',$y)" />
	</xsl:template>
	
	<xsl:decimal-format name="chf" decimal-separator="."
		grouping-separator="'" />

	<xsl:variable name="numberPattern">###'###'##0.00</xsl:variable>
	<xsl:template match="text()[contains(.,'.')]" name="numberFilter">
		<xsl:param name="n" select="." />
		<xsl:value-of select='format-number($n, $numberPattern, "chf")' />
	</xsl:template>

	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="my-page">
					<fo:region-body margin="1cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="my-page">
				<fo:flow flow-name="xsl-region-body">
					<fo:block margin-top="20mm" xsl:use-attribute-sets="title">
						<xsl:text>«Deckblatt Beleg»</xsl:text>
					</fo:block>
					<fo:block margin-top="20mm">
						<fo:table xsl:use-attribute-sets="tableProperties" width="75%" margin-left="12%">
							<fo:table-body>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block>
											Beleg Datum
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:call-template name="dateFilter">
												<xsl:with-param name="dd" select="data/date/." />
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block>
											Kategorie / Konto
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="data/cost-category/." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block></fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="data/account-number/." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block>
											Reisegrund / Erklärung
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="data/explanation/." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block>
											Betrag
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
										<xsl:text>CHF </xsl:text>
											<xsl:call-template name="numberFilter">
												<xsl:with-param name="n" select="data/amount/." />
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row xsl:use-attribute-sets="table-row">
									<fo:table-cell>
										<fo:block>
											Kostenstelle
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="data/project/." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>