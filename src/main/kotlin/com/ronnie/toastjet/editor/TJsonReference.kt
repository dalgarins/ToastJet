package com.ronnie.toastjet.editor

import com.intellij.json.psi.JsonReferenceExpression
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.impl.FakePsiElement
import com.intellij.util.ProcessingContext
import java.util.regex.Pattern


class TJsonReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(JsonStringLiteral::class.java),
            TjsonProvider()
        )
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(JsonReferenceExpression::class.java),
            TjsonProvider()
        )
    }
}


class TjsonProvider : PsiReferenceProvider() {

    override fun acceptsTarget(target: PsiElement): Boolean {
        return false
    }

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val text = element.text
        val references = mutableListOf<PsiReference>()

        val pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}")
        val matcher = pattern.matcher(text)

        while (matcher.find()) {
            val start = matcher.start(1)
            val end = matcher.end(1)

            val range = TextRange(start-2, end+2)
            references.add(TJsonPsiReference(element, range , {
            }))
        }

        return references.toTypedArray()
    }
}


class TJsonPsiReference(
    psiElement: PsiElement,
    textRange: TextRange,
    val runnable: Runnable
) :
    PsiReferenceBase<PsiElement?>(psiElement,textRange) {

    override fun resolve(): PsiElement {
        return FakeElement()
    }

    inner class FakeElement: FakePsiElement(), SyntheticElement {
        override fun getParent(): PsiElement {
            return element
        }

        override fun navigate(var1: Boolean) {
            runnable.run()
        }

        override fun getTextRange(): TextRange? {
            return textRange
        }

        override fun getName(): String {
            return ""
        }

        override fun getPresentableText(): String? {
            return text
        }
    }
}
