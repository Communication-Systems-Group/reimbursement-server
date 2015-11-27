<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" indent="yes" />

	<xsl:attribute-set name="marginTop5">
		<xsl:attribute name="margin-top">5pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="backgroundColor"
		use-attribute-sets="fontNormal">
		<xsl:attribute name="background-color">#ffffcc</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="fontNormal">
		<xsl:attribute name="font-size">9pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tableProperties">
		<xsl:attribute name="border-before-width.conditionality">retain</xsl:attribute>
		<xsl:attribute name="border-collapse">collapse</xsl:attribute>
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="fontBig">
		<xsl:attribute name="font-size">13pt</xsl:attribute>
		<xsl:attribute name="margin-top">1mm</xsl:attribute>
		<xsl:attribute name="margin-left">1mm</xsl:attribute>
		<xsl:attribute name="margin-right">1mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="headerCenterText">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">13pt</xsl:attribute>
		<xsl:attribute name="width">93mm</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="headerRightText">
		<xsl:attribute name="width">90mm</xsl:attribute>
		<xsl:attribute name="text-align">right</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="expenseFieldsLabel"
		use-attribute-sets="fontNormal">
		<xsl:attribute name="border">solid white 1px</xsl:attribute>
		<xsl:attribute name="padding-right">1mm</xsl:attribute>
		<xsl:attribute name="padding-left">1mm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="expenseFieldsText"
		use-attribute-sets="backgroundColor">
		<xsl:attribute name="border">solid white 1px</xsl:attribute>
		<xsl:attribute name="padding-right">1mm</xsl:attribute>
		<xsl:attribute name="padding-left">1mm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tableHeaderStyle">
		<xsl:attribute name="padding-right">1mm</xsl:attribute>
		<xsl:attribute name="padding-left">1mm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tableBodyStyle"
		use-attribute-sets="backgroundColor">
		<xsl:attribute name="border">solid white 1px</xsl:attribute>
		<xsl:attribute name="padding-right">1mm</xsl:attribute>
		<xsl:attribute name="padding-left">1mm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="footer">
		<xsl:attribute name="font-style">italic</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:variable name="numberOfExpenseItems">
		<xsl:value-of select="count(data/expense/expense-items)" />
	</xsl:variable>
	<xsl:variable name="numberOfConsolidatedExpenseItems">
		<xsl:value-of select="count(data/expense-items-consolidated)" />
	</xsl:variable>

	<!-- output filters START -->
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
	<!-- output filters END -->

	<xsl:template match="/">
		<fo:root font-family="Helvetica">
			<xsl:variable name="accountingText">
				<xsl:value-of select="data/expense/accounting/." />
			</xsl:variable>

			<xsl:variable name="expenseTotal">
				<xsl:value-of select="data/expense/total-amount/." />
			</xsl:variable>

			<xsl:variable name="expenseDate">
				<xsl:value-of select="data/expense/date/." />
			</xsl:variable>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4-landscape"
					page-height="21.0cm" page-width="29.7cm" margin-left="2cm"
					margin-right="2cm" margin-top="3mm">
					<fo:region-body margin-top="30mm" />
					<fo:region-before extent="23mm" region-name="fBefore" />
					<fo:region-after extent="15mm" region-name="fAfter" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<!-- Page one start -->
			<fo:page-sequence master-reference="A4-landscape"
				font-size="8pt">
				<fo:static-content flow-name="fBefore">
					<xsl:apply-templates select="data/expense" />
				</fo:static-content>
				<fo:static-content flow-name="fAfter">
					<fo:block xsl:use-attribute-sets="footer">
						Mit Unterschrift wird die
						Einhaltung des UZH-Spesenreglements bestätigt.
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:table table-layout="fixed" width="100%">
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:table xsl:use-attribute-sets="tableProperties"
										width="100%">
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell width="76mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsLabel"
														font-weight="bold">
														Spesenempfänger/in
														<fo:inline font-weight="normal">Vorname Nachname
														</fo:inline>
													</fo:block>
													<fo:block xsl:use-attribute-sets="expenseFieldsLabel">Personal-Nr.
														(bitte 0 weglassen)</fo:block>
												</fo:table-cell>
												<fo:table-cell width="93mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsText">
														<xsl:value-of select="data/expense/user/firstname/." />
														<xsl:text> </xsl:text>
														<xsl:value-of select="data/expense/user/lastname/." />
													</fo:block>
													<fo:block xsl:use-attribute-sets="expenseFieldsText">
														<xsl:value-of select="data/expense/user/personnel-number/." />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell width="76mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsLabel"
														font-weight="bold" margin-top="7pt">Kontaktperson</fo:block>
													<fo:block xsl:use-attribute-sets="expenseFieldsLabel">Telefonnummer
													</fo:block>
												</fo:table-cell>
												<fo:table-cell width="93mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsText"
														margin-top="7pt">
														<xsl:value-of select="data/expense/finance-admin/firstname/." />
														<xsl:text> </xsl:text>
														<xsl:value-of select="data/expense/finance-admin/lastname/." />
													</fo:block>
													<fo:block xsl:use-attribute-sets="expenseFieldsText">
														<xsl:value-of select="data/expense/finance-admin/phone-number/." />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell width="76mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsLabel"
														margin-top="7pt">Buchungstext (sichtbar im SAP)</fo:block>
												</fo:table-cell>
												<fo:table-cell width="93mm">
													<fo:block xsl:use-attribute-sets="expenseFieldsText"
														margin-top="7pt">
														<xsl:value-of select="data/expense/accounting/." />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:table-cell>
								<fo:table-cell width="76mm">
									<fo:table xsl:use-attribute-sets="tableProperties"
										width="100%">
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell>
													<fo:block>
														<fo:external-graphic src="url('img/uzh_card_new.png')"
															content-height="scale-to-fit" height="27mm"></fo:external-graphic>
													</fo:block>
													<fo:block>
														<fo:table width="100%" table-layout="fixed">
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell>
																		<fo:block>Datum:</fo:block>
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block>
																			<xsl:call-template name="dateFilter">
																				<xsl:with-param name="dd" select="$expenseDate" />
																			</xsl:call-template>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</fo:table-body>
														</fo:table>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<fo:block xsl:use-attribute-sets="marginTop5">
						<xsl:attribute name="border">1px solid</xsl:attribute>
						<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell width="7mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">Nr.</fo:block>
									</fo:table-cell>
									<fo:table-cell width="19mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">
											Beleg
											<fo:block />
											Datum
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="50mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">Kategorie / Konto</fo:block>
									</fo:table-cell>
									<fo:table-cell width="75mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">Reisegrund / Erklärung
										</fo:block>
										<fo:block>(Ort und Geschäftszweck)</fo:block>
									</fo:table-cell>
									<fo:table-cell width="18mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
										<fo:block font-weight="bold">
											Original
											<fo:block />
											Währung
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="23mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
										<fo:block font-weight="bold">Betrag</fo:block>
									</fo:table-cell>
									<fo:table-cell width="12mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
										<fo:block font-weight="bold">Kurs</fo:block>
									</fo:table-cell>
									<fo:table-cell width="23mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="right">
										<fo:block font-weight="bold">
											Betrag
											<fo:block />
											CHF
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="30mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
										<fo:block font-weight="bold">KST / PSP</fo:block>
										<fo:block>(Kostenstelle / Projekt)</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="9">
										<fo:block xsl:use-attribute-sets="fontBig"
											font-weight="bold">*** BITTE IMMER ORIGINALBELEGE EINREICHEN ***
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block margin-top="1mm">
						<xsl:attribute name="border">1px solid</xsl:attribute>
						<xsl:attribute name="padding">0mm</xsl:attribute>
						<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
							<fo:table-body>
								<xsl:apply-templates select="data/expense/expense-items" />
								<xsl:call-template name="repeat-yellow" />
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block margin-top="1mm">
						<xsl:attribute name="border">1px solid</xsl:attribute>
						<xsl:attribute name="padding">0mm</xsl:attribute>
						<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell width="129mm">
										<fo:block xsl:use-attribute-sets="fontBig"
											font-weight="bold">*** BITTE IMMER ORIGINALBELEGE EINREICHEN ***
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="75mm">
										<fo:block xsl:use-attribute-sets="fontBig">Auszahlungsbetrag:
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="23mm">
										<fo:block xsl:use-attribute-sets="fontBig"
											text-align="right">
											<xsl:call-template name="numberFilter">
												<xsl:with-param name="n" select="$expenseTotal" />
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="30mm" text-align="center">
										<fo:block font-size="13pt" margin-top="3px">CHF</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block margin-top="1mm">
						<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell width="76mm">
										<!-- signature field for expense creator -->
										<fo:block-container xsl:use-attribute-sets="backgroundColor"
											width="60mm" height="18mm">
											<xsl:if test="((data/user-signature != '') and (data/expense/has-digital-signature = 'false'))">
												<fo:block>
													<fo:external-graphic content-height="scale-to-fit"
														height="15mm">
														<xsl:attribute name="src">
															<xsl:text>url('data:image/png;base64,</xsl:text>
															<xsl:value-of select="data/user-signature/." />
															<xsl:text>')</xsl:text>
														</xsl:attribute>
													</fo:external-graphic>
												</fo:block>
											</xsl:if>
											<xsl:if test="((data/user-signature = '') or (data/expense/has-digital-signature = 'true'))">>
												<fo:block margin-top="13mm">
													<xsl:text> </xsl:text>
												</fo:block>
											</xsl:if>
										</fo:block-container>
									</fo:table-cell>
									<fo:table-cell width="181mm">
										<fo:block-container xsl:use-attribute-sets="backgroundColor"
											width="181mm" height="18mm">
											<fo:table table-layout="fixed" width="100%">
												<fo:table-body>
													<fo:table-row>
														<fo:table-cell width="145mm">
															<xsl:if test="((data/assigned-manager-signature != '') and (data/expense/has-digital-signature = 'false'))">
																<fo:block>
																	<fo:external-graphic
																		content-height="scale-to-fit" height="13mm">
																		<xsl:attribute name="src">
																			<xsl:text>url('data:image/png;base64,</xsl:text>
																			<xsl:value-of select="data/assigned-manager-signature/." />
																			<xsl:text>')</xsl:text>
																		</xsl:attribute>
																	</fo:external-graphic>
																</fo:block>
															</xsl:if>
															<xsl:if test="((data/assigned-manager-signature = '') or (data/expense/has-digital-signature = 'true'))">
																<fo:block margin-top="13mm">
																	<xsl:text> </xsl:text>
																</fo:block>
															</xsl:if>
															<fo:block>
																<fo:inline font-style="italic" font-size="12pt">
																	<xsl:if test="/data/manager-has-role-prof = 'true'">
																		<xsl:text>Prof. Dr. </xsl:text>
																	</xsl:if>
																	<xsl:value-of
																		select="data/expense/assigned-manager/firstname/." />
																	<xsl:text> </xsl:text>
																	<xsl:value-of
																		select="data/expense/assigned-manager/lastname/." />
																	<xsl:if test="/data/manager-has-role-prof = 'false'">
																		<xsl:text>, Department Manager</xsl:text>
																	</xsl:if>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell>
															<xsl:if	test="((data/finance-admin-signature != '') and (data/expense/has-digital-signature = 'false'))">
																<fo:block>
																	<fo:external-graphic
																		content-height="scale-to-fit" height="13mm">
																		<xsl:attribute name="src">
																			<xsl:text>url('data:image/png;base64,</xsl:text>
																			<xsl:value-of select="data/finance-admin-signature/." />
																			<xsl:text>')</xsl:text>
																		</xsl:attribute>
																	</fo:external-graphic>
																</fo:block>
															</xsl:if>
															<xsl:if test="((data/finance-admin-signature = '') or (data/expense/has-digital-signature = 'true'))">
																<fo:block margin-top="13mm">
																	<xsl:text> </xsl:text>
																</fo:block>
															</xsl:if>
															<fo:block margin-right="0mm">
																<fo:inline font-style="italic" font-size="12pt">Visum
																	formell</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block-container>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell width="76mm">
										<fo:block font-weight="bold">Unterschrift Spesenempfänger/in
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="181mm">
										<fo:block font-weight="bold">
											Unterschrift der finanziell verantwortlichen bzw.
											vorgesetzten Person
											<fo:inline font-weight="normal" font-size="8pt">(andere
												Person als Spesenempfänger/in)</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
			<!-- Page one end -->

			<!-- Page two start -->
			<fo:page-sequence master-reference="A4-landscape">
				<fo:static-content flow-name="fBefore">
					<xsl:apply-templates select="data/expense" />
				</fo:static-content>
				<fo:static-content flow-name="fAfter">
					<fo:block xsl:use-attribute-sets="footer">
						Mit Unterschrift wird die
						Einhaltung des UZH-Spesenreglements bestätigt.
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:block-container width="250mm" height="18mm"
						text-align="center">
						<fo:block xsl:use-attribute-sets="fontBig" font-weight="bold">
							Zusammenfassung (für Buchungszwecke - bitte immer mit ausdrucken)
						</fo:block>
					</fo:block-container>
					<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell width="76mm">
									<fo:block xsl:use-attribute-sets="expenseFieldsLabel"
										font-weight="bold">
										Spesensteller/in
										<fo:inline font-weight="normal">Vorname Nachname</fo:inline>
									</fo:block>
									<fo:block xsl:use-attribute-sets="expenseFieldsLabel">UZH Personalnummer
									</fo:block>
								</fo:table-cell>
								<fo:table-cell width="93mm">
									<fo:block xsl:use-attribute-sets="expenseFieldsText"
										background-color="white">
										<xsl:value-of select="data/expense/user/firstname/." />
										<xsl:text> </xsl:text>
										<xsl:value-of select="data/expense/user/lastname/." />
									</fo:block>
									<fo:block xsl:use-attribute-sets="expenseFieldsText"
										background-color="white">
										<xsl:value-of select="data/expense/user/personnel-number/." />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block xsl:use-attribute-sets="marginTop5">
						<xsl:attribute name="border">1px solid</xsl:attribute>
						<fo:table xsl:use-attribute-sets="tableProperties" width="100%">
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell width="22mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block />
									</fo:table-cell>
									<fo:table-cell width="50mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">Reisekosten Konto</fo:block>
									</fo:table-cell>
									<fo:table-cell width="50mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
										<fo:block font-weight="bold">
											Konto
											<fo:block />
											Nummer
										</fo:block>
									</fo:table-cell>
									<fo:table-cell width="75mm"
										xsl:use-attribute-sets="tableHeaderStyle">
										<fo:block font-weight="bold">Buchungstext</fo:block>
									</fo:table-cell>
									<fo:table-cell width="30mm"
										xsl:use-attribute-sets="tableHeaderStyle" text-align="right">
										<fo:block font-weight="bold">Betrag Brutto</fo:block>
									</fo:table-cell>
									<fo:table-cell border-left="1px solid #000000"
										width="30mm" xsl:use-attribute-sets="tableHeaderStyle"
										text-align="center">
										<fo:block font-weight="bold">KST / PSP</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block>
						<xsl:attribute name="border">1px solid</xsl:attribute>
						<fo:block>
							<fo:table xsl:use-attribute-sets="tableProperties"
								width="100%">
								<fo:table-footer>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="4"
											border-top="1px solid #000000">
											<fo:block xsl:use-attribute-sets="fontBig">Auszahlungsbetrag:
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border-top="1px solid #000000">
											<fo:block xsl:use-attribute-sets="fontBig"
												font-weight="bold" text-align="right">
												<xsl:call-template name="numberFilter">
													<xsl:with-param name="n" select="$expenseTotal" />
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border-top="1px solid #000000"
											border-left="1px solid #000000">
											<fo:block />
										</fo:table-cell>
									</fo:table-row>
								</fo:table-footer>
								<fo:table-body>
									<xsl:for-each select="data/expense-items-consolidated">
										<xsl:variable name="i" select="position()" />
										<fo:table-row>
											<fo:table-cell width="22mm"
												xsl:use-attribute-sets="tableBodyStyle" background-color="white">
												<fo:block>
													<xsl:value-of select="$i" />
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="50mm"
												xsl:use-attribute-sets="tableBodyStyle" background-color="white">
												<fo:block>
													<xsl:value-of select="cost-category-name" />
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="50mm"
												xsl:use-attribute-sets="tableBodyStyle" text-align="center"
												background-color="white">
												<fo:block>
													<xsl:value-of select="account-number" />
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="75mm"
												xsl:use-attribute-sets="tableBodyStyle" background-color="white">
												<fo:block>
													<xsl:copy-of select="$accountingText" />
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="30mm"
												xsl:use-attribute-sets="tableBodyStyle" text-align="right"
												background-color="white">
												<fo:block>
													<xsl:call-template name="numberFilter">
														<xsl:with-param name="n" select="total-amount" />
													</xsl:call-template>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell border-left="1px solid #000000"
												width="30mm" xsl:use-attribute-sets="tableBodyStyle"
												background-color="white" text-align="center">
												<fo:block>
													<xsl:value-of select="project" />
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:for-each>
									<xsl:call-template name="repeat-white" />
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block>
					<xsl:if test="data/qrcode != ''">
						<fo:block text-align="right" margin-top="15mm"
							margin-right="-6mm">
							<fo:external-graphic content-height="scale-to-fit"
								height="40mm">
								<xsl:attribute name="src">
									<xsl:text>url('data:image/png;base64,</xsl:text>
									<xsl:value-of select="data/qrcode/." />
									<xsl:text>')</xsl:text>
								</xsl:attribute>
							</fo:external-graphic>
						</fo:block>
					</xsl:if>
					<fo:block text-align="right" margin-top="-5mm" font-size="8pt">
						<fo:block>
							<xsl:value-of select="data/url/." />
						</fo:block>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
			<!-- Page two end -->

			<!-- Page three start -->
			<!-- <fo:page-sequence master-reference="A4-landscape"> <fo:static-content 
				flow-name="fBefore"> <xsl:apply-templates select="data/expense" /> </fo:static-content> 
				<fo:static-content flow-name="fAfter"> <fo:block xsl:use-attribute-sets="footer"> 
				Mit Unterschrift wird die Einhaltung des UZH-Spesenreglements bestätigt. 
				</fo:block> </fo:static-content> <fo:flow flow-name="xsl-region-body"> <fo:block 
				text-align="center"> <fo:external-graphic> <xsl:attribute name="src"> <xsl:text>url('data:image/png;base64,</xsl:text> 
				<xsl:value-of select="data/qrcode/."/> <xsl:text>')</xsl:text> </xsl:attribute> 
				</fo:external-graphic> <fo:block><xsl:value-of select="data/url/."/></fo:block> 
				</fo:block> </fo:flow> </fo:page-sequence> -->
			<!-- Page three end -->
		</fo:root>
	</xsl:template>

	<xsl:template match="data/expense">
			<fo:block>
				<fo:table xsl:use-attribute-sets="tableProperties" width="100%" end-indent="-20mm">
					<fo:table-body>
						<fo:table-row block-progression-dimension.maximum="20mm">
							<fo:table-cell width="56mm">
								<fo:block>
									<fo:external-graphic src="url(img/uzh_logo.gif)"
										content-height="scale-to-fit" height="20mm" margin-top="-20mm"></fo:external-graphic>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="headerCenterText">
								<fo:block margin-top="10mm">
									Spesenabrechnung UZH-Angestellte
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="headerRightText">
								<fo:block margin-top="3mm">Finanzen</fo:block>
								<fo:block>Finanzielles Rechnungswesen</fo:block>
								<fo:block>Kreditoren</fo:block>
								<fo:block>Hirschengraben 60</fo:block>
								<fo:block>CH-8001 Zürich</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
	</xsl:template>

	<xsl:template match="expense-items">
		<xsl:variable name="i" select="position()" />
		<fo:table-row>
			<fo:table-cell width="7mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:value-of select="$i" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="19mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:call-template name="dateFilter">
						<xsl:with-param name="dd" select="date" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle">
				<fo:block>
					<xsl:value-of select="cost-category/name/de" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="75mm" xsl:use-attribute-sets="tableBodyStyle">
				<fo:block>
					<xsl:value-of select="explanation" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="18mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:value-of select="currency" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="right">
				<fo:block>
					<xsl:call-template name="numberFilter">
						<xsl:with-param name="n" select="original-amount" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="12mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="right">
				<fo:block>
					<xsl:choose>
						<xsl:when test="currency != 'CHF'">
							<xsl:call-template name="numberFilter">
								<xsl:with-param name="n" select="exchange-rate" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> </xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle"
				background-color="white" text-align="right">
				<fo:block>
					<xsl:call-template name="numberFilter">
						<xsl:with-param name="n" select="calculated-amount" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="30mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:value-of select="cost-category/account-number" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="repeat-items-yellow">
		<xsl:variable name="i" select="position()" />
		<fo:table-row>
			<fo:table-cell width="7mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block color="#ffffcc">
					<xsl:value-of select="$i" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="19mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="75mm" xsl:use-attribute-sets="tableBodyStyle">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="18mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="right">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="12mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="right">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle"
				background-color="white" text-align="right">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="30mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="repeat-items-white">
		<xsl:variable name="i" select="position()" />
		<fo:table-row>
			<fo:table-cell width="22mm" xsl:use-attribute-sets="tableBodyStyle"
				background-color="white">
				<fo:block color="#ffffff">
					<xsl:value-of select="$i" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle"
				background-color="white">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="center" background-color="white">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="75mm" xsl:use-attribute-sets="tableBodyStyle"
				background-color="white">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell width="30mm" xsl:use-attribute-sets="tableBodyStyle"
				text-align="right" background-color="white">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-left="1px solid #000000" width="30mm"
				xsl:use-attribute-sets="tableBodyStyle" background-color="white"
				text-align="center">
				<fo:block>
					<xsl:text> </xsl:text>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="repeat-yellow">
		<xsl:param name="count" select="15 - $numberOfExpenseItems" />
		<xsl:if test="$count">
			<xsl:call-template name="repeat-items-yellow" />
			<xsl:call-template name="repeat-yellow">
				<xsl:with-param name="count" select="$count - 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="repeat-white">
		<xsl:param name="count" select="15 - $numberOfConsolidatedExpenseItems" />
		<xsl:if test="$count">
			<xsl:call-template name="repeat-items-white" />
			<xsl:call-template name="repeat-white">
				<xsl:with-param name="count" select="$count - 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>