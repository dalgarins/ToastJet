package com.ronnie.toastjet.editor

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.codeinsight.JsonStandardComplianceInspection
import com.intellij.json.psi.*
import com.intellij.psi.PsiElementVisitor


class TJsonLocalInspection : JsonStandardComplianceInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
      return TJsonValidator(holder)
    }


    private inner class TJsonValidator(holder: ProblemsHolder?) :
        JsonStandardComplianceInspection.StandardJsonValidatingElementVisitor( holder) {

        override fun allowComments(): Boolean = true
        override fun allowNanInfinity(): Boolean = true
        override fun allowSingleQuotes(): Boolean = true
        override fun allowTrailingCommas(): Boolean = true
        override fun allowIdentifierPropertyNames(): Boolean = true

        override fun visitStringLiteral(stringLiteral: JsonStringLiteral) {
        }

        override fun visitReferenceExpression(reference: JsonReferenceExpression) {

        }
    }
}

