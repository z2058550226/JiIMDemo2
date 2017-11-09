package com.suikajy.jiimdemo2.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Created by zjy on 2017/11/1.
 */

/**
 * 非空单例委托，只能被赋值一次
 */
fun <T> notNullSingleValue(): ReadWriteProperty<Any?, T> = NotNullSingleValueVar()

private class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value
        else throw IllegalAccessException("this single var has already initialized")
    }
}

/**
 * 获取元数据委托
 */
fun appMetaData(context: Context, name: String): ReadOnlyProperty<Any?, String> = MetaData(context, name)

private class MetaData(val context: Context, val name: String) : ReadOnlyProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName,
                    PackageManager.GET_META_DATA)
            val value = appInfo.metaData.getString(name)
            value
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }
}

/**
 * SharedPreference委托
 */
fun <T : Any> preference(context: Context, key: String, default: T): ReadWriteProperty<Any?, T> = Preference(context, key, default)

private class Preference<T>(val context: Context, val key: String, val default: T) : ReadWriteProperty<Any?, T> {
    val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("default_shared_preference", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> findPreference(name: String, default: T): T = with(preferences) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can not be saved into Preferences")
        }
        res as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun <U> putPreference(name: String, value: U) = with(preferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }
}