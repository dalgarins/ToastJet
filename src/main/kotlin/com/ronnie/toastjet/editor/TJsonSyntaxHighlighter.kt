package com.ronnie.toastjet.editor

import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory
import com.intellij.lexer.Lexer

class TJsonSyntaxHighlighter : JsonSyntaxHighlighterFactory() {
    override fun getLexer(): Lexer {
        return TJsonLexer()
    }

    override fun isCanEscapeEol(): Boolean = true
}