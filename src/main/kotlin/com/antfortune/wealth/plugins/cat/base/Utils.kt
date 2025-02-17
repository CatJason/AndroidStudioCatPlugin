package com.antfortune.wealth.plugins.cat.base

import com.intellij.psi.PsiClass

fun getClassHierarchy(psiClass: PsiClass): List<PsiClass> {
    val hierarchy = mutableListOf<PsiClass>()
    var currentClass: PsiClass? = psiClass

    while (currentClass != null) {
        hierarchy.add(currentClass)
        currentClass = currentClass.superClass
    }

    return hierarchy
}