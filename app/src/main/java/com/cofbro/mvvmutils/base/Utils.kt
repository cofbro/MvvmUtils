@file:Suppress("UNCHECKED_CAST")

package com.cofbro.mvvmutils.base

import android.content.Context
import java.lang.reflect.ParameterizedType

internal fun <T> Any.findActualGenericsClass(cls: Class<*>): Class<T>? {
    val genericSuperclass = javaClass.genericSuperclass
    if (genericSuperclass !is ParameterizedType) {
        return null
    }
    // 获取类的所有泛型参数数组
    val actualTypeArguments = genericSuperclass.actualTypeArguments
    // 遍历泛型数组
    actualTypeArguments.forEach {
        if (it is Class<*> && cls.isAssignableFrom(it)) {
            return it as Class<T>
        } else if (it is ParameterizedType) {
            val rawType = it.rawType
            if (rawType is Class<*> && cls.isAssignableFrom(rawType)) {
                return rawType as Class<T>
            }
        }
    }
    return null
}


const val SP_USER_NAME = "username"
const val SP_PASSWORD = "password"
const val SP_USER_ID = "userId"
const val SP_EMAIL = "email"

fun Context.saveUsedSp(key: String, value: String) {
    val editor = getSharedPreferences("sp_data", Context.MODE_PRIVATE).edit()
    editor.putString(key, value).apply()
}

fun Context.getBySp(key: String): String? {
    val sp = getSharedPreferences("sp_data", Context.MODE_PRIVATE)
    return sp.getString(key, "")
}
