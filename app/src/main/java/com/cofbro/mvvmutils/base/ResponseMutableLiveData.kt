package com.cofbro.mvvmutils.base

/**
 * 一个针对于实体类数据的可变LiveData，与 ResponseObserver 配合使用
 *
 * 提供 postValue() 和 setValue()
 * @see ResponseObserver
 * @see postValue
 * @see setValue
 *
 */
class ResponseMutableLiveData<T> : ResponseLiveData<T> {

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

    public override fun postValue(value: BaseResponse<T>?) {
        super.postValue(value)
    }

    public override fun setValue(value: BaseResponse<T>?) {
        super.setValue(value)
    }
}