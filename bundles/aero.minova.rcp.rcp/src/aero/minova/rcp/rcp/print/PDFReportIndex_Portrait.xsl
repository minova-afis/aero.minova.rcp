<?xml version="1.0" encoding="%%Encoding%%"?>
<!DOCTYPE localizing PUBLIC "localizing" "%%ReportPath%%/logo.dtd">
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="%%ReportPath%%/date-time.xsl"/>
	<xsl:decimal-format decimal-separator="," grouping-separator="." name="de"/>
	<xsl:template match="%%XMLMainTag%%">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master margin-bottom="1mm" margin-left="10mm" margin-right="10mm" margin-top="10mm" master-name="simple" page-height="297mm"
					page-width="210mm" rule-style="solid" rule-thickness="1px">
					<fo:region-body margin-bottom="10mm" margin-top="21mm" region-name="xsl-region-body"/>
					<fo:region-before extent="23mm" font-family="serif" region-name="xsl-region-before" rule-style="solid" rule-thickness="1px"/>
					<fo:region-after extent="10mm" region-name="xsl-region-after" rule-style="solid" rule-thickness="1px"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:for-each select="IndexView">
				<fo:page-sequence master-reference="simple">
					<fo:static-content flow-name="xsl-region-before">
						<fo:block-container absolute-position="absolute" top="&logo-portrait-top;" left="&logo-portrait-left;" width="&logo-portrait-width;"
							height="&logo-portrait-height;" background-image="%%ReportPath%%/LogoPortrait.png" background-repeat="no-repeat">
							<fo:block/>
						</fo:block-container>
						<fo:block>
							<fo:table table-layout="fixed" width="100%">
								<fo:table-column column-width="65mm"/>
								<fo:table-column column-width="60mm"/>
								<fo:table-column column-width="25mm"/>
								<fo:table-column column-width="40mm"/>
								<fo:table-body>
									<fo:table-row>
									    <fo:table-cell font-family="%%FontFamily%%" font-size="11pt">
											<fo:block line-height="13pt" text-align="start" wrap-option="no-wrap">
												<xsl:value-of select="../Site/Address1"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="start" wrap-option="no-wrap">
												<xsl:value-of select="../Site/Address2"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="start" wrap-option="no-wrap">
												<xsl:value-of select="../Site/Address3"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell font-weight="bold">
											<fo:block font-size="14pt" line-height="14pt" text-align="center">%%tAddress.Index%%: %%FormTitle%%</fo:block>
										</fo:table-cell>
									    <fo:table-cell font-family="%%FontFamily%%" font-size="11pt" padding-left="7mm">
											<fo:block line-height="13pt" text-align="start">%%tAddress.Phone%%:</fo:block>
											<fo:block line-height="13pt" text-align="start">%%tAddress.Fax%%:</fo:block>
											<fo:block line-height="13pt" text-align="start">%%tDate%%:</fo:block>
										</fo:table-cell>
									    <fo:table-cell font-family="%%FontFamily%%" font-size="11pt">
											<fo:block line-height="13pt" text-align="end">
												<xsl:value-of select="../Site/Phone"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="end">
												<xsl:value-of select="../Site/Fax"/>
											</fo:block>
											<fo:block line-height="13pt" text-align="end">
												<xsl:value-of select="../PrintDate"/>
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
							<fo:table-column column-width="30mm"/>
							<fo:table-column column-width="130mm"/>
							<fo:table-column column-width="30mm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="start">
											<!-- <xsl:value-of select="../Site/Application"/> -->
											Free Tables
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="center">MINOVA Information Services GmbH</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="end">
											%%tAddress.Page%%
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
	    <fo:table font-size="%%FontSizeCell%%pt" font-family="%%FontFamily%%" table-layout="fixed" width="100%">
			%%ColumnDefinition%%
			<fo:table-header border-color="black" border-width="1pt" padding-bottom="1mm">
				<fo:table-row font-weight="normal">%%TableTitle%%</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:apply-templates select="Row"/>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="%%ColumnCount%%">
						<fo:block font-size="0pt" line-height="2mm" text-align-last="justify">
							<fo:leader leader-pattern="rule" rule-style="solid" rule-thickness="1px"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<xsl:template match="Row">
		<fo:table-row>%%CellDefinition%%</fo:table-row>
	</xsl:template>
	<xsl:template match="SumRow">
		<fo:table font-size="%%FontSizeCell%%pt" table-layout="fixed" width="100%">
			%%ColumnDefinition%%
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
					%%SumCellDefinition%%</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
</xsl:stylesheet>