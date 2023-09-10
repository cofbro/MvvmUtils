package com.cofbro.mvvmutils.base

import androidx.lifecycle.MutableLiveData
import com.cofbro.mvvmutils.lean.LeanCloudUtils

/**
 * Repository的父类，此类真正用作数据请求
 * @see executeRequest
 */
open class BaseRepository {

    // Loading 状态的 LiveData
    val loadingStateLiveData: MutableLiveData<LoadingState> by lazy {
        MutableLiveData<LoadingState>()
    }

    val leanCloudLiveData by lazy {
        leanCloudUtils.leanCloudLiveData
    }

    protected val leanCloudUtils by lazy {
        LeanCloudUtils()
    }

    /**
     * 发起请求，通过判断接收到的数据进行网络状态的更新，并通过postValue为实体类赋值
     *  @param responseLiveData 观察请求结果的LiveData
     *  @param showLoading 是否展示加载提示
     *  @param loadingMsg 加载提示中的信息
     *  @param block 真正执行的函数回调，通常是retrofit api
     *
     */
    suspend fun <T : Any> executeRequest(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        var response = BaseResponse<T>()
        try {
            if (showLoading) {
                loadingStateLiveData.postValue(LoadingState(loadingMsg, DataState.STATE_LOADING))
            }
            response = block()
            if (response.code == BaseResponse.ERROR_CODE_SUCCESS) {
                if (isEmptyData(response.data)) {
                    response.dataState = DataState.STATE_EMPTY
                } else {
                    response.dataState = DataState.STATE_SUCCESS
                }
            } else {
                response.dataState = DataState.STATE_FAILED
            }
        } catch (e: Exception) {
            response.dataState = DataState.STATE_ERROR
            response.exception = e
        } finally {
            responseLiveData.postValue(response)
            if (showLoading) {
                loadingStateLiveData.postValue(LoadingState(loadingMsg, DataState.STATE_FINISH))
            }
        }
    }

    fun executeLCRequest(block: () -> Unit) {
        block()
    }

    private fun <T> isEmptyData(data: T?): Boolean {
        return data == null || data is List<*> && (data as List<*>).isEmpty()
    }


}