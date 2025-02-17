package com.antfortune.wealth.plugins.cat

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiClass
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder

class GetContextInspection : AbstractBaseJavaLocalInspectionTool() {

    private val logger = Logger.getInstance(GetContextInspection::class.java)

    // 创建检查方法，遍历代码
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): JavaElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)

                logger.info("检查方法调用: ${expression.text}")  // 日志记录方法调用信息

                // 获取当前方法调用的所属类
                val containingClass = PsiTreeUtil.getParentOfType(expression, PsiClass::class.java)

                if (containingClass != null) {
                    // 先判断类是否继承自 Fragment
                    val isFragmentSubclass = isFragmentSubclass(containingClass)

                    if (isFragmentSubclass) {
                        logger.info("该类是 Fragment 的子类，继续检查方法调用")

                        // 判断方法是否是 getContext
                        if (expression.methodExpression.referenceName == "getContext") {
                            logger.info("检测到 'getContext' 方法调用")

                            // 注册问题（错误）
                            holder.registerProblem(
                                expression,
                                "不允许在 Fragment 中使用 getContext 方法喵\n调用 'getContext()' 可能导致空指针异常：Fragment 可能尚未附加到 Activity，返回 null",
                                ProblemHighlightType.ERROR,
                                DeleteContextCallQuickFix()
                            )

                            logger.info("已注册问题：删除 Fragment 中的 getContext 方法调用")
                        }
                    }
                }
            }
        }
    }

    // 判断类是否继承自 Fragment
    private fun isFragmentSubclass(psiClass: PsiClass): Boolean {
        val classHierarchy = getClassHierarchy(psiClass)
        return classHierarchy.any { it.qualifiedName == "androidx.fragment.app.Fragment" }
    }

    // 获取类的继承链
    private fun getClassHierarchy(psiClass: PsiClass): List<PsiClass> {
        val hierarchy = mutableListOf<PsiClass>()
        var currentClass: PsiClass? = psiClass

        while (currentClass != null) {
            hierarchy.add(currentClass)
            currentClass = currentClass.superClass
        }

        return hierarchy
    }
}
