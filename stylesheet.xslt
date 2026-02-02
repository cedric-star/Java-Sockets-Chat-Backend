<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <title>MP3 List</title>
                <style>
                    body {
                    background-color: <xsl:value-of select="files/style/@backgroundColor"/>;
                    color: <xsl:value-of select="files/style/@mainTextColor"/>;

                    font-family: verdana;
                    }

                    th {
                    background-color: <xsl:value-of select="files/style/@tableHeadBackgroundColor"/>;
                    color: <xsl:value-of select="files/style/@tableHeadTextColor"/>;

                    padding: 10px;
                    border: none;
                    }

                    td {
                    background-color: <xsl:value-of select="files/style/@tableRowBackgroundColor"/>;
                    color: <xsl:value-of select="files/style/@tableRowTextColor"/>;

                    padding: 10px;
                    border: none;
                    }

                    table {
                    border: none;
                    width: 100%;
                    }
                </style>
            </head>
            <body>
                <h1>MP3 Files for user <xsl:value-of select="files/@user"/></h1>
                <h4>sorted by: <xsl:value-of select="files/@sortby"/>, in <xsl:value-of select="files/@sortorder"/> order</h4>
                <table border="1">
                    <tr><th>Title</th><th>Artist</th><th>Album</th><th>Genre</th><th>Duration</th></tr>
                    <xsl:for-each select="files/mp3">
                        <xsl:sort select="*[name() = /files/@sortby]" order="*[name() = /files/@sortorder]"/>
                        <tr>
                            <td><xsl:value-of select="title"/></td>
                            <td><xsl:value-of select="artist"/></td>
                            <td><xsl:value-of select="album"/></td>
                            <td><xsl:value-of select="genre"/></td>
                            <td><xsl:value-of select="duration"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>