package com.cofbro.mvvmutils.base

/**
 * 请求得到数据后的实体类
 * @param T 请求中的<var>data</var>字段，与返回数据中的data相对应
 */
class BaseResponse<T>(
    var code: Int = 0,
    var message: String? = null,
    var data: T? = null,
    var dataState: DataState? = null,
    var exception: Throwable? = null,
) {
    companion object {
        const val ERROR_CODE_SUCCESS = 0
    }

    val success: Boolean
        get() = code == ERROR_CODE_SUCCESS
}

/**
 * 网络请求状态
 * @property STATE_LOADING 开始请求
 * @property STATE_SUCCESS 服务器请求成功
 * @property STATE_EMPTY 服务器返回数据为 null
 * @property STATE_FAILED 接口请求成功但是服务器返回 error
 * @property STATE_ERROR 请求失败
 * @property STATE_FINISH 请求结束
 */
enum class DataState {
    STATE_INITIALIZE,
    STATE_LOADING,
    STATE_SUCCESS,
    STATE_EMPTY,
    STATE_FAILED,
    STATE_ERROR,
    STATE_FINISH,
}