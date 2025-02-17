package com.antfortune.wealth.plugins.cat

import com.intellij.openapi.project.Project
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElementFactory

class DeleteContextCallQuickFix : LocalQuickFix {

    private val logger = Logger.getInstance(DeleteContextCallQuickFix::class.java)

    // 快速修复名称
    override fun getName(): String {
        return "将 getContext 替换为 getContextOrNull"
    }

    // 修复操作的家族名称
    override fun getFamilyName(): String {
        return "替换不允许的调用"
    }

    // 实现修复操作：将 getContext 方法调用替换为 getContextOrNull
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is PsiMethodCallExpression) {
            logger.info("正在将 'getContext' 方法调用替换为 'getContextOrNull': ${element.text}")

            // 获取当前的方法调用名称
            val methodName = element.methodExpression.referenceName

            // 检查是否是 getContext 调用
            if (methodName == "getContext") {
                // 获取 PsiElementFactory 用于创建新的方法调用
                val factory = PsiElementFactory.SERVICE.getInstance(project)

                // 创建新的方法引用（getContextOrNull）
                val newMethodReference = factory.createExpressionFromText("getContextOrNull", null)

                // 替换旧的 methodExpression 为新的方法引用
                element.methodExpression.replace(newMethodReference)
                logger.info("已将 'getContext' 替换为 'getContextOrNull'")
            } else {
                logger.warn("修复操作失败：未找到 'getContext' 方法调用")
            }
        } else {
            logger.warn("修复操作失败：无法找到 getContext 方法调用")
        }
    }
}