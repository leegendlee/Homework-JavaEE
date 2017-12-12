<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:variable name="inForm" select="html/body/form"></xsl:variable>
    <xsl:variable name="inTextView" select="$inForm/input/textView"></xsl:variable>
    <xsl:variable name="inButtonView" select="$inForm/buttonView"></xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <xsl:value-of select="html/head"/>
            </head>
            <body>
                <xsl:element name="form">
                    <xsl:attribute name="name">
                        <xsl:value-of select="$inForm/name"></xsl:value-of>
                    </xsl:attribute>
                    <xsl:attribute name="action">
                        <xsl:value-of select="$inForm/action"></xsl:value-of>
                    </xsl:attribute>
                    <xsl:attribute name="method">
                        <xsl:value-of select="$inForm/method"></xsl:value-of>
                    </xsl:attribute>
                    <xsl:for-each select="$inTextView">
                        <label>
                            <xsl:value-of select="label"></xsl:value-of>
                        </label>
                        <xsl:element name="input">
                            <xsl:attribute name="name">
                                <xsl:value-of select="name"></xsl:value-of>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                                <xsl:value-of select="value"></xsl:value-of>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                    <label>
                        <xsl:value-of select="$inButtonView/name"></xsl:value-of>
                    </label>
                    <xsl:element name="input">
                        <xsl:attribute name="type">
                            <xsl:text>submit</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="name">
                            <xsl:value-of select="$inButtonView/name"></xsl:value-of>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>