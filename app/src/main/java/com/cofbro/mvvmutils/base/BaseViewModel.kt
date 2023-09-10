package com.cofbro.mvvmutils.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel层：向View提供数据，向MRepository请求数据
 * @param T ViewModel默认持有Repository，通过Repository发起网络请求
 */
abstract class BaseViewModel<T : BaseRepository> : ViewModel() {
    protected val repository: T by lazy {
        createRepository()
    }

    val leanCloudLiveData by lazy {
        repository.leanCloudLiveData
    }

    val loadingDataState: LiveData<LoadingState> by lazy {
        repository.loadingStateLiveData
    }

    /**
     * 反射创建Repository
     * @return T Repository
     */
    @Suppress("UNCHECKED_CAST")
    open fun createRepository(): T {
        val baseRepository = findActualGenericsClass<T>(BaseRepository::class.java)
            ?: throw NullPointerException("Can not find a BaseRepository Generics in ${javaClass.simpleName}")
        return baseRepository.newInstance()
    }
}