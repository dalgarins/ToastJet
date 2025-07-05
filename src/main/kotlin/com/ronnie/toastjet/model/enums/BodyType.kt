package com.ronnie.toastjet.model.enums

import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.PlainTextLanguage

enum class BodyType {
    None,
    FormData,
    URLEncoded,
    Binary,
    RAW
}

enum class RawType {
    JSON,
    XML,
    TEXT,
    HTML,
    JS,
    GraphQL
}

enum class EditorContentType {
    JSON,
    XML,
    MD,
    HTML,
    YAML,
    PT;

    fun getLanguage(): Language {
        return when (this) {
            EditorContentType.JSON -> JsonLanguage.INSTANCE
            EditorContentType.XML -> XMLLanguage.INSTANCE
            EditorContentType.MD -> Language.findLanguageByID("Markdown") ?: PlainTextLanguage.INSTANCE
            EditorContentType.HTML -> HTMLLanguage.INSTANCE
            EditorContentType.YAML -> Language.findLanguageByID("yaml") ?: PlainTextLanguage.INSTANCE
            EditorContentType.PT -> PlainTextLanguage.INSTANCE
        }
    }
}

enum class ContentType(val value: Language) {
    JSON(JsonLanguage.INSTANCE),
    XML(XMLLanguage.INSTANCE),
    HTML(HTMLLanguage.INSTANCE),
    YAML(Language.findLanguageByID("yaml") ?: PlainTextLanguage.INSTANCE),
    JAVASCRIPT(Language.findLanguageByID("javascript") ?: PlainTextLanguage.INSTANCE),
    PLAIN_TEXT(PlainTextLanguage.INSTANCE)
}