package com.ronnie.toastjet.editor

import com.intellij.psi.PsiElement
import com.jetbrains.jsonSchema.extension.JsonLikePsiWalker
import com.jetbrains.jsonSchema.extension.JsonLikePsiWalkerFactory
import com.jetbrains.jsonSchema.impl.JsonOriginalPsiWalker
import com.jetbrains.jsonSchema.impl.JsonSchemaObject


object TJsonPsiWalker : JsonOriginalPsiWalker() {
    override fun requiresNameQuotes(): Boolean = false

    override fun allowsSingleQuotes(): Boolean = false
}

class TJsonLikePsiWalker: JsonLikePsiWalkerFactory {

    override fun handles(element: PsiElement): Boolean = true

    override fun create(schemaObject: JsonSchemaObject?): JsonLikePsiWalker = TJsonPsiWalker

}