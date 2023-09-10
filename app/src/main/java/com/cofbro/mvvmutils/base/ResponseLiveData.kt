package com.cofbro.mvvmutils.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

/**
 * 一个针对于实体类数据的不可变LiveData，与 ResponseObserver 配合使用
 * @see ResponseObserver
 */
abstract class ResponseLiveData<T> : LiveData<BaseResponse<T>> {
    /**
     * Creates a MutableLiveData initialized with the given `value`.
     *
     *  @param  value initial value
     */
    constructor(value: BaseResponse<T>?) : super(value)

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    constructor() : super()

    /**
     * Adds the given observer to the observers list within the lifespan of the given owner.
     * The events are dispatched on the main thread. If LiveData already has data set, it
     * will be delivered to the observer.
     */
    fun observe(owner: LifecycleOwner, observer: ResponseObserver<T>) {
        super.observe(owner, observer)
    }
}