<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE localizing PUBLIC "localizing" "/Applications/SIS.app/reports//logo.dtd">
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="/Applications/SIS.app/reports//date-time.xsl"/>
	<xsl:decimal-format decimal-separator="," grouping-separator="." name="de"/>
	<xsl:template match="xvctsTestStreckeIndex">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master margin-bottom="1mm" margin-left="10mm" margin-right="10mm" margin-top="10mm" master-name="simple" page-height="210mm"
					page-width="297mm" rule-style="solid" rule-thickness="1px">
					<fo:region-body margin-bottom="10mm" margin-top="21mm" region-name="xsl-region-body"/>
					<fo:region-before extent="23mm" font-family="serif" region-name="xsl-region-before" rule-style="solid" rule-thickness="1px"/>
					<fo:region-after extent="10mm" region-name="xsl-region-after" rule-style="solid" rule-thickness="1px"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:for-each select="IndexView">
				<fo:page-sequence master-reference="simple">
					<fo:static-content flow-name="xsl-region-before">
						<fo:block-container absolute-position="absolute" top="&logo-landscape-top;" left="&logo-landscape-left;" width="&logo-landscape-width;"
							height="&logo-landscape-height;" background-image="/Applications/SIS.app/reports//LogoLandscape.png" background-repeat="no-repeat">
							<fo:block/>
						</fo:block-container>
						<fo:block>
							<fo:table table-layout="fixed" width="100%">
								<fo:table-column column-width="65mm"/>
								<fo:table-column column-width="147mm"/>
								<fo:table-column column-width="25mm"/>
								<fo:table-column column-width="5mm"/>
								<fo:table-column column-width="35mm"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell font-family="Humanist521 BT, ArialUni" font-size="11pt">
											<fo:block line-height="13pt" text-align="start">
												<xsl:value-of select="../Site/Address1"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="start">
												<xsl:value-of select="../Site/Address2"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="start">
												<xsl:value-of select="../Site/Address3"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell font-weight="bold">
											<fo:block font-size="14pt" line-height="14pt" text-align="center">Index: Verwaltung Stundenerfassung</fo:block>
										</fo:table-cell>
										<fo:table-cell padding-right="3mm">
											<fo:block>
												<fo:external-graphic content-width="60pt" content-height="40pt">
													<xsl:attribute name="src">
														<xsl:value-of select="concat('url(', ../Site/Logo, ')')"/>
													</xsl:attribute>
												</fo:external-graphic>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell font-family="Humanist521 BT, ArialUni" font-size="11pt">
											<fo:block line-height="13pt" text-align="start">Tel.:</fo:block>
											<fo:block line-height="13pt" text-align="start">Fax:</fo:block>
											<fo:block line-height="13pt" text-align="start">Datum:</fo:block>
										</fo:table-cell>
										<fo:table-cell font-family="Humanist521 BT, ArialUni" font-size="11pt">
											<fo:block line-height="13pt" text-align="end">
												<xsl:value-of select="../Site/Phone"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="end">
												<xsl:value-of select="../Site/Fax"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="end">
												<xsl:call-template name="format-date-time">
													<xsl:with-param name="datum" select="../PrintDate"/>
													<xsl:with-param name="locale" select="&locale;"/>
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							<fo:block font-size="0pt" line-height="2mm" text-align-last="justify" margin-top="1mm">
								<fo:leader leader-pattern="rule" rule-style="solid" rule-thickness="1px"/>
							</fo:block>
							<fo:block size="20mm"/>
						</fo:block>
					</fo:static-content>
					<fo:static-content flow-name="xsl-region-after">
						<fo:block font-size="0pt" line-height="2mm" text-align-last="justify">
							<fo:leader leader-pattern="rule" rule-style="solid" rule-thickness="1px"/>
						</fo:block>
						<fo:table font-family="sans-serif" font-size="9pt" table-layout="fixed" width="100%">
							<fo:table-column column-width="50mm"/>
							<fo:table-column column-width="177mm"/>
							<fo:table-column column-width="50mm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="start">
											<xsl:value-of select="../Site/Application"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="center">MINOVA Information Services GmbH</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="end">
											Seite
											<fo:page-number/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body">
						<xsl:apply-templates select="Group"/>
						<xsl:apply-templates select="Rows"/>
					</fo:flow>
				</fo:page-sequence>
			</xsl:for-each>
		</fo:root>
	</xsl:template>
	<xsl:template match="Group">
		<fo:block color="white">.</fo:block>
		<xsl:if test="name(..) != 'IndexView'">
			<fo:block-container overflow="hidden">
				<fo:block overflow="hidden" font-weight="bold" text-align="start" font-size="10pt">
					<xsl:value-of select="GroupText"/>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:apply-templates select="Group"/>
		<xsl:apply-templates select="Rows"/>
		<xsl:apply-templates select="SumRow"/>
	</xsl:template>
	<xsl:template match="Rows">
		<fo:table font-size="8pt" font-family="Arial, ArialUni, Helvetica" table-layout="fixed" width="100%">
			<fo:table-column column-width="13mm"/>
<fo:table-column column-width="15mm"/>
<fo:table-column column-width="52mm"/>
<fo:table-column column-width="35mm"/>
<fo:table-column column-width="108mm"/>
<fo:table-column column-width="54mm"/>

			<fo:table-header border-color="black" border-width="1pt" padding-bottom="1mm">
				<fo:table-row font-weight="normal"><fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="solid" border-right-style="none" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block>Datum</fo:block>
	</fo:block-container>
	
</fo:table-cell>
<fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="none" border-right-style="none" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block>Mitarbeiter/in</fo:block>
	</fo:block-container>
	
</fo:table-cell>
<fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="none" border-right-style="none" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block>Kunde</fo:block>
	</fo:block-container>
	
</fo:table-cell>
<fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="none" border-right-style="none" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block>Von</fo:block>
	</fo:block-container>
	
</fo:table-cell>
<fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="none" border-right-style="none" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block>Bis</fo:block>
	</fo:block-container>
	
</fo:table-cell>
<fo:table-cell font-size="8pt" background-color="lightgrey" padding-right="1mm" wrap-option="no-wrap" border-left-style="none" border-right-style="solid" border-bottom-style="solid" border-top-style="solid" text-align="left">
	<fo:block-container overflow="hidden">
		<fo:block></fo:block>
	</fo:block-container>
	
</fo:table-cell>
</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:apply-templates select="Row"/>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="6">
						<fo:block font-size="0pt" line-height="2mm" text-align-last="justify">
							<fo:leader leader-pattern="rule" rule-style="solid" rule-thickness="1px"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<xsl:template match="Row">
		<fo:table-row><fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block wrap-option="no-wrap" text-align="left">
			<xsl:value-of select="BookingDate"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block wrap-option="no-wrap" text-align="left">
			<xsl:value-of select="EmployeeText"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block wrap-option="no-wrap" text-align="left">
			<xsl:value-of select="CustomerText"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block wrap-option="no-wrap" text-align="left">
			<xsl:value-of select="StartDate"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block wrap-option="no-wrap" text-align="left">
			<xsl:value-of select="EndDate"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block text-align="center">
		</fo:block>
	</fo:block-container>
</fo:table-cell>
</fo:table-row>
	</xsl:template>
	<xsl:template match="SumRow">
		<fo:table font-size="8pt" table-layout="fixed" width="100%">
			<fo:table-column column-width="13mm"/>
<fo:table-column column-width="15mm"/>
<fo:table-column column-width="52mm"/>
<fo:table-column column-width="35mm"/>
<fo:table-column column-width="108mm"/>
<fo:table-column column-width="54mm"/>

			<fo:table-body>
				<fo:table-row>
					<fo:table-cell padding-right="1mm" wrap-option="no-wrap">
						<fo:block-container>
							<fo:block font-weight="bold" text-align="start">
								<xsl:if test="../Text != ''">
									<xsl:value-of select="../Text"/>
								</xsl:if>
								<xsl:if test="../Value != ''">
									:
									<xsl:value-of select="../Value"/>
								</xsl:if>
							</fo:block>
						</fo:block-container>
					</fo:table-cell>
					
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block overflow="hidden" font-weight="bold" text-align="end">
			<xsl:value-of select="EmployeeText"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block overflow="hidden" font-weight="bold" text-align="end">
			<xsl:value-of select="CustomerText"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block overflow="hidden" font-weight="bold" text-align="end">
			<xsl:value-of select="StartDate"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block overflow="hidden" font-weight="bold" text-align="end">
			<xsl:value-of select="EndDate"/>
		</fo:block>
	</fo:block-container>
</fo:table-cell>
<fo:table-cell padding-right="1mm">
	<fo:block-container overflow="hidden">
		<fo:block text-align="center">
		</fo:block>
	</fo:block-container>
</fo:table-cell>
</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
</xsl:stylesheet>