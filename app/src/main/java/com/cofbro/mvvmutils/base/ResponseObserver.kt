package com.cofbro.mvvmutils.base

import androidx.lifecycle.Observer
import com.hjq.toast.ToastUtils

/**
 * 创建一个观察实体类对象的观察者，一旦实体类的数据发生变化(调用postValue())，
 * 观察者观察到后，则会调用onChanged()，触发请求状态更新后的回调。
 * @param T -> 响应得到的实体类，对应被观察数据 LiveData<T> 中的泛型
 */
abstract class ResponseObserver<T> : Observer<BaseResponse<T>> {

    final override fun onChanged(response: BaseResponse<T>?) {
        response?.let {
            when (response.dataState) {
                DataState.STATE_SUCCESS, DataState.STATE_EMPTY -> {
                    onSuccess(response.data)
                }
                DataState.STATE_FAILED -> {
                    onFailure(response.message, response.code)
                }
                DataState.STATE_ERROR -> {
                    onException(response.exception)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 请求抛出异常
     * @param  exception 异常对象
     */
    private fun onException(exception: Throwable?) {
        ToastUtils.show(exception.toString())
    }

    /**
     * 请求成功
     * @param  data 请求数据
     */
    abstract fun onSuccess(data: T?)

    /**
     * 请求失败
     * @param  errorCode 错误码
     * @param  errorMsg 错误信息
     */
    open fun onFailure(errorMsg: String?, errorCode: Int) {
        ToastUtils.show("Login Failed, errorCode: $errorCode, errorMsg: $errorMsg")
    }
}