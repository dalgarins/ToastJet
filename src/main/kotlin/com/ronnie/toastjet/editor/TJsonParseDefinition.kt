package com.ronnie.toastjet.editor

import com.intellij.json.json5.Json5ParserDefinition
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType


class TJsonParseDefinition : Json5ParserDefinition() {

    override fun createLexer(project: Project?): Lexer = TJsonLexer()


    override fun createParser(project: Project?): PsiParser = JsonParser()


    override fun createFile(fileview: FileViewProvider): PsiFile {
        return JsonFileImpl(fileview, TJsonLanguage.INSTANCE)
    }

    override fun getFileNodeType(): IFileElementType {
        return elementType
    }
}

val elementType = IFileElementType(TJsonLanguage.INSTANCE)