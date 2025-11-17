<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Chat Nachrichten</title>
                <style>
                    body {
                    font-family: Arial, sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: #f5f5f5;
                    }
                    .chat-container {
                    background-color: white;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    padding: 20px;
                    }
                    .message {
                    border-bottom: 1px solid #eee;
                    padding: 15px 0;
                    }
                    .message:last-child {
                    border-bottom: none;
                    }
                    .user {
                    font-weight: bold;
                    color: #2c3e50;
                    margin-bottom: 5px;
                    }
                    .timestamp {
                    font-size: 0.8em;
                    color: #7f8c8d;
                    margin-bottom: 8px;
                    }
                    .msg {
                    color: #34495e;
                    line-height: 1.4;
                    }
                    .header {
                    text-align: center;
                    color: #2c3e50;
                    margin-bottom: 30px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Chat Verlauf</h1>
                </div>
                <div class="chat-container">
                    <xsl:apply-templates select="messages/message"/>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="message">
        <div class="message">
            <div class="user">
                <xsl:value-of select="user"/>
            </div>
            <div class="timestamp">
                <xsl:call-template name="format-timestamp">
                    <xsl:with-param name="timestamp" select="timestamp"/>
                </xsl:call-template>
            </div>
            <div class="msg">
                <xsl:value-of select="msg"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="format-timestamp">
        <xsl:param name="timestamp"/>
        <xsl:variable name="date" select="substring($timestamp, 1, 10)"/>
        <xsl:variable name="time" select="substring($timestamp, 12, 8)"/>
        <xsl:value-of select="concat($date, ' ', $time)"/>
    </xsl:template>
</xsl:stylesheet>