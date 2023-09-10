package com.cofbro.mvvmutils.lean

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.leancloud.LCFile
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import cn.leancloud.types.LCNull
import com.cofbro.mvvmutils.base.DataState
import com.cofbro.mvvmutils.base.LoadingState
import com.hjq.toast.ToastUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.HashMap


/**
 *
 * LeanCloud 在结构中类似于Retrofit API，也可以就把他当做Retrofit API来使用
 *
 * LeanCloud 工具类，包含注册，登录，储存，删除，查询等通用Api
 *
 * 内部同样封装好了请求状态，使用者无需关心
 *
 * 注意：如果需要用此工具包，需要设置全局参数 isUsedLeanCloud
 *
 * 如果设置了全局参数，但部分页面并未使用 LeanCloudUtils，
 *
 * 可以重写 Activity/Fragment 中的 isUseLeanCloud()
 *
 * @see BaseFragment.isUseLeanCloud()
 * @see BaseActivity.isUseLeanCloud()
 */
class LeanCloudUtils {

    val leanCloudLiveData by lazy {
        MutableLiveData<LoadingState>()
    }

    companion object {
        private var isUseLeanCloud = false
        fun init(ifUseLeanCloud: Boolean) {
            this.isUseLeanCloud = ifUseLeanCloud
        }

        fun isUsed(): Boolean {
            return isUseLeanCloud
        }
    }


    /**
     * 储存
     * @param className LeanCloud对应的表名称
     * @param map 待储存对象中的参数，用键值对储存
     * @param onSuccess 成功回调
     */
    fun <T : Any> saveInLC(
        className: String,
        map: HashMap<String, T>,
        msg: String?,
        onSuccess: (LCObject) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        val todo = LCObject(className)
        map.forEach { (t, u) ->
            todo.put(t, u)
        }
        todo.saveInBackground().subscribe(ObserverImpl<LCObject>(msg) {
            onSuccess(it)
        })
    }

    /**
     * 储存
     * @param lcObject LeanCloud对应的表对象
     * @param msg 请求成功展示信息
     * @param onSuccess 成功回调
     */
    fun saveInLC(
        lcObject: LCObject,
        msg: String?,
        onSuccess: (LCObject) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        lcObject.saveInBackground().subscribe(ObserverImpl<LCObject>(msg) {
            onSuccess(it)
        })
    }

    /**
     * 查询
     * @param className LeanCloud对应的表名称
     * @param equalMap 查询条件
     * @param msg 请求成功展示信息
     * @param onSuccess 成功回调
     */
    fun searchInLC(
        className: String,
        equalMap: HashMap<String, String>?,
        msg: String?,
        onSuccess: (List<LCObject>) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        val todo = LCQuery<LCObject>(className)
        todo.orderByDescending("createdAt")
        equalMap?.forEach { (t, u) ->
            todo.whereEqualTo(t, u)
        }
        todo.findInBackground().subscribe(ObserverImpl<List<LCObject>>(msg) {
            onSuccess(it)
        })
    }

    /**
     * 更新
     * @param className LeanCloud对应的表名称
     * @param objectId 更新对象id
     * @param map 待储存对象中的参数，用键值对储存
     * @param onSuccess 成功回调
     */
    fun updateInLC(
        className: String,
        objectId: String,
        msg: String,
        map: HashMap<String, String>,
        onSuccess: (LCObject) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        val todo = LCObject.createWithoutData(className, objectId)
        map.forEach { (t, u) ->
            todo.put(t, u)
        }
        todo.saveInBackground().subscribe(ObserverImpl<LCObject>(msg) {
            onSuccess(it)
        })
    }

    /**
     * 保存
     * @param filename LeanCloud对应的表名称
     * @param path 文件的绝对路径
     * @param msg 附带信息
     * @param onSuccess 成功回调
     */
    fun saveFileInLC(
        filename: String,
        path: String,
        msg: String?,
        onSuccess: (LCFile) -> Unit = {}
    ) {
        val file = LCFile.withAbsoluteLocalPath(filename, path)
        file.saveInBackground().subscribe(ObserverImpl<LCFile>(msg) {
            Log.d("chy", "saveFileInLC: file-> ${it.url}")
            onSuccess(it)
        })
    }


    /**
     *  将文件进行关联
     * @param className LeanCloud对应的表名称
     * @param files 待关联的文件
     * @param map 待上传的参数
     */
    fun attachOn(className: String, files: List<LCFile>, map: HashMap<String, String>) {
        val todo = LCObject(className)
        map.forEach { (t, u) ->
            todo.put(t, u)
        }
        files.forEach {
            todo.add("attachments", it)
        }
        todo.save()
    }

    /**
     * 注册，需要验证邮箱，否则注册不成功
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱地址
     * @param isTeacher 是否是老师
     * @param onSuccess 成功回调
     */
    fun register(
        username: String,
        password: String,
        email: String,
        isTeacher: Boolean,
        msg: String,
        onSuccess: (LCUser) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        val user = LCUser()
        user.username = username
        user.password = password
        user.email = email
        user.put("isTeacher", isTeacher)
        user.signUpInBackground().subscribe(ObserverImpl<LCUser>(msg) {
            onSuccess(it)
        })
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param onSuccess 成功回到
     */
    fun login(
        username: String,
        password: String,
        msg: String,
        onSuccess: (LCUser) -> Unit = {}
    ) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        LCUser.logIn(username, password).subscribe(ObserverImpl<LCUser>(msg) {
            onSuccess(it)
        })

    }

    /**
     * 判断是否是登录状态
     */
    fun isLogin(): Boolean {
        return LCUser.getCurrentUser() != null
    }

    /**
     * 登出，云端登出
     */
    fun logout() {
        LCUser.logOut()
    }

    /**
     * 重置密码
     * @param email 邮箱地址
     * @param onSuccess 成功回调
     */
    fun resetPassword(email: String, msg: String, onSuccess: () -> Unit) {
        checkIsUsed()
        leanCloudLiveData.postValue(LoadingState(null, DataState.STATE_LOADING))
        LCUser.requestPasswordResetInBackground(email).subscribe(ObserverImpl<LCNull>(msg) {
            onSuccess()
        })
    }

    /**
     * 是否使用 LeanCloud
     */
    private fun checkIsUsed() {
        if (!isUsed()) return
    }

    /**
     * reactivex.Observer 实现类，防止每次 new 一个匿名内部类
     * @param onSuccess 成功回调
     */
    inner class ObserverImpl<T : Any>(private val msg: String?, val onSuccess: (T) -> Unit = {}) :
        Observer<T> {
        override fun onSubscribe(d: Disposable) {}

        override fun onNext(t: T) {
            Log.d("chy", "post")
            leanCloudLiveData.postValue(LoadingState(msg, DataState.STATE_SUCCESS))
            onSuccess(t)
        }

        override fun onError(e: Throwable) {
            ToastUtils.show(e)
        }

        override fun onComplete() {}
    }
}