package com.antfortune.wealth.plugins.cat.recycleview
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil

class AddPositionCheckQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "添加越界检查"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        // 获取包含问题的 PsiMethod
        val method = descriptor.psiElement as? PsiMethod ?: return

        // 创建越界检查的代码片段
        val checkCode =
            "if (position < 0 || position >= data.size()) {\n" +
                    "    return; // RecycleView Adapter 的 onBindViewHolder 存在数组越界的概率，需要做越界检查\n" +
                    "}"

        // 使用 JavaPsiFacade 获取 PsiElementFactory
        val elementFactory = JavaPsiFacade.getElementFactory(project)

        // 找到方法体中的第一个语句
        val firstStatement = PsiTreeUtil.findChildOfType(method.body, PsiStatement::class.java)
        if (firstStatement != null) {
            // 在第一个语句之前插入越界检查代码
            method.body?.addBefore(elementFactory.createStatementFromText(checkCode, null), firstStatement)
        } else {
            // 如果方法体为空，直接添加代码到方法体中
            method.body?.add(elementFactory.createStatementFromText(checkCode, null))
        }
    }
}