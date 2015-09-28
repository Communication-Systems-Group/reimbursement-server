<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" indent="yes"/>

    <xsl:attribute-set name="marginTop5">
        <xsl:attribute name="margin-top">5pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="backgroundColor" use-attribute-sets="fontNormal">
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

    <xsl:attribute-set name="expenseFieldsLabel" use-attribute-sets="fontNormal">
        <xsl:attribute name="border">solid white 1px</xsl:attribute>
        <xsl:attribute name="padding-right">1mm</xsl:attribute>
        <xsl:attribute name="padding-left">1mm</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="expenseFieldsText" use-attribute-sets="backgroundColor">
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

    <xsl:attribute-set name="tableBodyStyle" use-attribute-sets="backgroundColor">
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

    <xsl:template match="/">
        <fo:root>
            <xsl:variable name="accountingText">
                <xsl:value-of select="expense/header/accountingText/."/>
            </xsl:variable>

            <xsl:variable name="expenseTotal">
                <xsl:value-of select="expense/expenseItems/total/."/>
            </xsl:variable>

            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-landscape"
                                       page-height="21.0cm"
                                       page-width="29.7cm"
                                       margin-left="2cm"
                                       margin-right="2cm"
                                       margin-top="3mm">
                    <fo:region-body margin-top="30mm"/>
                    <fo:region-before extent="15mm" region-name="fBefore"/>
                    <fo:region-after extent="15mm" region-name="fAfter"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <!-- Page one start -->
            <fo:page-sequence master-reference="A4-landscape" font-size="8pt">
                <fo:static-content flow-name="fBefore">
                    <xsl:apply-templates select="expense/header" />
                </fo:static-content>
                <fo:static-content flow-name="fAfter">
                    <fo:block xsl:use-attribute-sets="footer">
                        Mit Unterschrift wird die Einhaltung des UZH-Spesenreglements bestätigt.
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:table xsl:use-attribute-sets="tableProperties">
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell width="76mm">
                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel" font-weight="bold">
                                        Spesenempfänger/in
                                        <fo:inline font-weight="normal">Vorname Nachname</fo:inline>
                                    </fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel">Personal-Nr. (bitte 0 weglassen)</fo:block>

                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel" font-weight="bold" margin-top="7pt">Kontaktperson</fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel">Telefonnummer</fo:block>

                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel" margin-top="7pt">Buchungstext (sichtbar im SAP)</fo:block>
                                </fo:table-cell>
                                <fo:table-cell width="93mm">
                                    <fo:block xsl:use-attribute-sets="expenseFieldsText">
                                        <xsl:value-of select="expense/header/expenseReceiver/name/."/>
                                    </fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsText">
                                        <xsl:value-of select="expense/header/expenseReceiver/number/."/>
                                    </fo:block>

                                    <fo:block xsl:use-attribute-sets="expenseFieldsText" margin-top="7pt">
                                        <xsl:value-of select="expense/header/contactPerson/name/."/>
                                    </fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsText">
                                        <xsl:value-of select="expense/header/contactPerson/phone/."/>
                                    </fo:block>

                                    <fo:block xsl:use-attribute-sets="expenseFieldsText" margin-top="7pt">
                                        <xsl:value-of select="expense/header/accountingText/."/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell width="76mm">
                                    <fo:block>
                                        <fo:external-graphic src="url(img/uzh_card.png)"></fo:external-graphic>
                                    </fo:block>
                                    <fo:block margin-left="24mm">
                                        <fo:table width="30mm">
                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell>
                                                        <fo:block>Datum:</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell>
                                                        <fo:block>
                                                            <xsl:value-of select="expense/info/expenseDate/." />
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

                    <fo:block xsl:use-attribute-sets="marginTop5">
                        <xsl:attribute name="border">1px solid</xsl:attribute>
                        <fo:table xsl:use-attribute-sets="tableProperties">
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell width="7mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Nr.</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="19mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Beleg<fo:block/>Datum</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="50mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Kategorie / Konto</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="75mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Reisegrund / Erklärung</fo:block>
                                        <fo:block>(Ort und Geschäftszweck)</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="18mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">Original<fo:block/>Währung</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="23mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">Betrag</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="12mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">Kurs</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="23mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="right">
                                        <fo:block font-weight="bold">Betrag<fo:block/>CHF</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="30mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">KST / PSP</fo:block>
                                        <fo:block>(Kostenstelle / Projekt)</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell number-columns-spanned="9">
                                        <fo:block xsl:use-attribute-sets="fontBig" font-weight="bold">*** BITTE IMMER ORIGINALBELEGE EINREICHEN ***</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block margin-top="1mm">
                        <xsl:attribute name="border">1px solid</xsl:attribute>
                        <xsl:attribute name="padding">0mm</xsl:attribute>
                        <fo:table xsl:use-attribute-sets="tableProperties">
                            <fo:table-body>
                                <xsl:apply-templates select="expense/expenseItems/items/item"/>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block margin-top="1mm">
                        <xsl:attribute name="border">1px solid</xsl:attribute>
                        <xsl:attribute name="padding">0mm</xsl:attribute>
                        <fo:table xsl:use-attribute-sets="tableProperties">
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell width="129mm">
                                        <fo:block xsl:use-attribute-sets="fontBig" font-weight="bold">*** BITTE IMMER ORIGINALBELEGE EINREICHEN ***</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="75mm">
                                        <fo:block  xsl:use-attribute-sets="fontBig">Auszahlungsbetrag:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="23mm">
                                        <fo:block  xsl:use-attribute-sets="fontBig" text-align="right">
                                            <xsl:value-of select="expense/expenseItems/total/."/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="30mm" text-align="center">
                                        <fo:block font-size="13pt">CHF</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block margin-top="1mm">
                        <fo:table xsl:use-attribute-sets="tableProperties">
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell width="76mm">
                                        <fo:block-container xsl:use-attribute-sets="backgroundColor" width="60mm" height="18mm">
                                            <fo:block></fo:block>
                                        </fo:block-container>
                                    </fo:table-cell>
                                    <fo:table-cell width="181mm">
                                        <fo:block-container xsl:use-attribute-sets="backgroundColor" width="181mm" height="18mm">
                                            <fo:block></fo:block>
                                        </fo:block-container>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell width="76mm">
                                        <fo:block font-weight="bold">Unterschrift Spesenempfängerin</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="181mm">
                                        <fo:block font-weight="bold">
                                            Unterschrift der finanziell verantwortlichen bzw. vorgesetzten Person
                                            <fo:inline font-weight="normal" font-size="8pt">(andere Person als Spesenempfänger/in)</fo:inline>
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
                    <xsl:apply-templates select="expense/header" />
                </fo:static-content>
                <fo:static-content flow-name="fAfter">
                    <fo:block xsl:use-attribute-sets="footer">
                        Mit Unterschrift wird die Einhaltung des UZH-Spesenreglements bestätigt.
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block-container width="250mm" height="18mm" text-align="center">
                        <fo:block xsl:use-attribute-sets="fontBig" font-weight="bold">
                            Zusammenfassung (für Buchungszwecke - bitte immer mit ausdrucken)
                        </fo:block>
                    </fo:block-container>
                    <fo:table xsl:use-attribute-sets="tableProperties">
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell width="76mm">
                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel" font-weight="bold">
                                        Spesensteller/in
                                        <fo:inline font-weight="normal">Vorname Nachname</fo:inline>
                                    </fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsLabel">UZH Personalnummer</fo:block>
                                </fo:table-cell>
                                <fo:table-cell width="93mm">
                                    <fo:block xsl:use-attribute-sets="expenseFieldsText" background-color="white">
                                        <xsl:value-of select="expense/header/expenseApplicant/name/."/>
                                    </fo:block>
                                    <fo:block xsl:use-attribute-sets="expenseFieldsText" background-color="white">
                                        <xsl:value-of select="expense/header/expenseApplicant/number/."/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>

                    <fo:block xsl:use-attribute-sets="marginTop5">
                        <xsl:attribute name="border">1px solid</xsl:attribute>
                        <fo:table xsl:use-attribute-sets="tableProperties">
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell width="22mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block />
                                    </fo:table-cell>
                                    <fo:table-cell width="50mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Reisekosten Konto</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="50mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">Konto<fo:block />Nummer</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="75mm" xsl:use-attribute-sets="tableHeaderStyle">
                                        <fo:block font-weight="bold">Buchungstext</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell width="30mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="right">
                                        <fo:block font-weight="bold">Betrag Brutto</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border-left="1px solid #000000" width="30mm" xsl:use-attribute-sets="tableHeaderStyle" text-align="center">
                                        <fo:block font-weight="bold">KST / PSP</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                    <fo:block>
                        <xsl:attribute name="border">1px solid</xsl:attribute>
                        <fo:block>
                            <fo:table xsl:use-attribute-sets="tableProperties">
                                <fo:table-footer>
                                    <fo:table-row >
                                        <fo:table-cell number-columns-spanned="4" border-top="1px solid #000000">
                                            <fo:block xsl:use-attribute-sets="fontBig">Auszahlungsbetrag:</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border-top="1px solid #000000">
                                            <fo:block xsl:use-attribute-sets="fontBig" font-weight="bold" text-align="right">
                                                <fo:block><xsl:copy-of select="$expenseTotal"/></fo:block>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border-top="1px solid #000000" border-left="1px solid #000000">
                                            <fo:block />
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-footer>
                                <fo:table-body>
                                    <xsl:for-each select="expense/expenseItems/items/item">
                                        <fo:table-row>
                                            <fo:table-cell width="22mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                                                <fo:block><xsl:value-of select="id"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle">
                                                <fo:block><xsl:value-of select="category"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                                                <fo:block><xsl:value-of select="accountNumber"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell width="75mm" xsl:use-attribute-sets="tableBodyStyle">
                                                <fo:block><xsl:copy-of select="$accountingText"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell width="30mm" xsl:use-attribute-sets="tableBodyStyle" text-align="right">
                                                <fo:block><xsl:value-of select="amount"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell border-left="1px solid #000000" width="30mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                                                <fo:block><xsl:value-of select="account"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
            <!-- Page two end -->
        </fo:root>
    </xsl:template>

    <xsl:template match="expense/header">
        <fo:block>
            <fo:table xsl:use-attribute-sets="tableProperties">
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell width="76mm">
                            <fo:block>
                                <fo:external-graphic src="url(img/uzh_logo.png)" margin-top="-20mm"></fo:external-graphic>
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

    <xsl:template match="item">
        <fo:table-row>
            <fo:table-cell width="7mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                <fo:block><xsl:value-of select="id"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="19mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                <fo:block><xsl:value-of select="date"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="50mm" xsl:use-attribute-sets="tableBodyStyle">
                <fo:block><xsl:value-of select="category"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="75mm" xsl:use-attribute-sets="tableBodyStyle">
                <fo:block><xsl:value-of select="reason"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="18mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                <fo:block><xsl:value-of select="currencyOriginal"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle" text-align="right">
                <fo:block><xsl:value-of select="amount"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="12mm" xsl:use-attribute-sets="tableBodyStyle" text-align="right">
                <fo:block><xsl:value-of select="currency"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="23mm" xsl:use-attribute-sets="tableBodyStyle" text-align="right">
                <fo:block><xsl:value-of select="amount"/></fo:block>
            </fo:table-cell>
            <fo:table-cell width="30mm" xsl:use-attribute-sets="tableBodyStyle" text-align="center">
                <fo:block><xsl:value-of select="account"/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>
</xsl:stylesheet>