<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- 
     Datum von einer Datumsangabe ausgeben 
     Input: <ClosingUntil>2014-03-22</ClosingUntil>
     Aufruf: <xsl:call-template name="format-date">
                 <xsl:with-param name="datum" select="ClosingUntil"/>
                 <xsl:with-param name="locale">&locale;</xsl:with-param>
             </xsl:call-template>
     Als Anwender IMMER NUR das Template ohne Locale-Angabe verwenden !!
    -->
	<xsl:template name="format-date">
		<xsl:param name="datum"/>
		<xsl:param name="locale"/>
		<xsl:if test="string-length($datum)!=0">
			<xsl:choose>
				<xsl:when test="$locale = 'de' or $locale = 'de_DE' or $locale = 'de_CH'">
					<xsl:call-template name="private-format-date_de">
						<xsl:with-param name="datum" select="$datum"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="private-format-date_de">
						<xsl:with-param name="datum" select="$datum"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="private-format-date_de">
		<xsl:param name="datum"/>
		<xsl:value-of select="substring-after(substring-after($datum,'-'),'-')"/>.<xsl:value-of select="substring-before(substring-after($datum, '-'), '-')"
			/>.<xsl:value-of select="substring-before($datum, '-')"/>
	</xsl:template>



	<!-- 
     Datum und Zeit von einer Datum und Zeitangabe ausgeben 
     Input: <PrintDate>2014-03-22 06:41</PrintDate>
     Aufruf: <xsl:call-template name="format-date">
                 <xsl:with-param name="datum" select="PrintDate"/>
                 <xsl:with-param name="locale">&locale;</xsl:with-param>
             </xsl:call-template>
     Als Anwender IMMER NUR das Template ohne Locale-Angabe verwenden !!
    -->
	<xsl:template name="format-date-time">
		<xsl:param name="datum"/>
		<xsl:param name="locale"/>
		<xsl:if test="string-length($datum)=10">
			<xsl:call-template name="format-date">
				<xsl:with-param name="datum">
					<xsl:value-of select="$datum"/>
				</xsl:with-param>
				<xsl:with-param name="locale">
					<xsl:value-of select="$locale"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="string-length($datum)>10">
			<xsl:choose>
				<xsl:when test="$locale = 'de' or $locale = 'de_DE' or $locale = 'de_CH'">
					<xsl:call-template name="private-format-date-time_de">
						<xsl:with-param name="datum" select="$datum"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="private-format-date-time_de">
						<xsl:with-param name="datum" select="$datum"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="private-format-date-time_de">
		<xsl:param name="datum"/>
		<xsl:value-of select="substring-after(substring-after(substring-before($datum, ' '),'-'),'-')"/>.<xsl:value-of
			select="substring-before(substring-after($datum, '-'), '-')"/>.<xsl:value-of select="substring-before($datum, '-')"/>
		<fo:inline color="white">.</fo:inline><xsl:value-of select="substring-before(substring-after($datum, ' '), ':')"/>:<xsl:value-of
			select="substring-after($datum, ':')"/>
	</xsl:template>



	<!-- 
     Datum von einer Datum und Zeitangabe ausgeben 
     Input: <PrintDate>2014-03-22 06:41</PrintDate>
     Aufruf: <xsl:call-template name="format-date">
                 <xsl:with-param name="datum" select="PrintDate"/>
                 <xsl:with-param name="locale">&locale;</xsl:with-param>
             </xsl:call-template>
     Als Anwender IMMER NUR das Template ohne Locale-Angabe verwenden !!
    -->
	<xsl:template name="format-date-time-to-date">
		<xsl:param name="datum"/>
		<xsl:param name="locale"/>
		<xsl:choose>
			<xsl:when test="$locale = 'de' or $locale = 'de_DE' or $locale = 'de_CH'">
				<xsl:call-template name="private-format-date-time-to-date_de">
					<xsl:with-param name="datum" select="$datum"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="private-format-date-time-to-date_de">
					<xsl:with-param name="datum" select="$datum"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="private-format-date-time-to-date_de">
		<xsl:param name="datum"/>
		<xsl:value-of select="substring-after(substring-after(substring-before($datum, ' '),'-'),'-')"/>.<xsl:value-of
			select="substring-before(substring-after($datum, '-'), '-')"/>.<xsl:value-of select="substring-before($datum, '-')"/>
	</xsl:template>



</xsl:stylesheet>
