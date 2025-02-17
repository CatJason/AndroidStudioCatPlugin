package com.antfortune.wealth.plugins.cat.recycleview

import com.antfortune.wealth.plugins.cat.base.getClassHierarchy
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

class RecycleViewInspection : AbstractBaseJavaLocalInspectionTool() {

    // 创建检查方法，遍历代码
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): JavaElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)

                val psiClass = method.containingClass ?: return
                if (!isRecyclerViewAdapterSubclass(psiClass)) {
                    return
                }
                if (method.name != "onBindViewHolder") {
                    return
                }

                // 获取方法体
                val methodBody = method.body
                val ifStatements = PsiTreeUtil.findChildrenOfType(methodBody, PsiIfStatement::class.java)

                var hasPositionCheck = false

                // 检查是否存在对 position 的越界检查
                for (ifStatement in ifStatements) {
                    val condition = ifStatement.condition
                    if (condition != null && isPositionOutOfBoundsCheck(condition)) {
                        hasPositionCheck = true
                        break
                    }
                }

                if (!hasPositionCheck) {
                    // 如果没有越界检查，注册一个问题
                    holder.registerProblem(
                        method,
                        "onBindViewHolder需要对position进行越界检查喵",
                        ProblemHighlightType.GENERIC_ERROR,
                        AddPositionCheckQuickFix()
                    )
                }

            }

            // 判断类是否继承自 RecyclerView.Adapter
            private fun isRecyclerViewAdapterSubclass(psiClass: PsiClass): Boolean {
                // 获取类的继承链
                val classHierarchy = getClassHierarchy(psiClass)
                return classHierarchy.any {
                    it.qualifiedName == "androidx.recyclerview.widget.RecyclerView.Adapter"
                }
            }

            // 检查条件是否为越界检查
            private fun isPositionOutOfBoundsCheck(condition: PsiExpression): Boolean {
                // 这里可以根据实际的越界检查逻辑来判断
                // 例如检查类似于 "position < 0 || position >= size" 这样的条件
                return condition.text.contains("position") && (condition.text.contains("< 0") || condition.text.contains(
                    ">="
                ))
            }
        }
    }
}
