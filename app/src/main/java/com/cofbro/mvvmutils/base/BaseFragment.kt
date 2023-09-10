package com.cofbro.mvvmutils.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cofbro.mvvmutils.lean.LeanCloudUtils
import com.hjq.toast.ToastUtils
import java.lang.reflect.Modifier

abstract class BaseFragment<VM : BaseViewModel<*>, VB : ViewBinding> : Fragment() {
    protected var container: ViewGroup? = null
    protected var mContext: Context? = null


    protected val viewModel by lazy {
        createViewModel()
    }

    protected val binding by lazy {
        createViewBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.container = container
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadingDataState.observe(requireActivity()) {
            when (it.state) {
                DataState.STATE_LOADING ->
                    showLoading(it.msg)
                else ->
                    dismissLoading(it.msg)
            }
        }
        if (LeanCloudUtils.isUsed() && isUsedLeanCloud()) {
            viewModel.leanCloudLiveData.observe(requireActivity()) {
                when (it.state) {
                    DataState.STATE_INITIALIZE -> {}
                    DataState.STATE_LOADING ->
                        showLoading(it.msg)
                    DataState.STATE_SUCCESS -> {
                        dismissLoading(it.msg)
                    }
                    else -> {}
                }
            }
        }
        onAllViewCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.mContext = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.leanCloudLiveData.value = LoadingState(null, DataState.STATE_INITIALIZE)
    }

    /**
     *  当onCreate方法执行完毕前就会执行此方法，相当于暴露给用户的<var>onViewCreated</var>
     *  @param  savedInstanceState savedInstanceState
     *  @see onCreate
     */
    abstract fun onAllViewCreated(savedInstanceState: Bundle?)

    /**
     * 显示请求 loading 中的提示
     */
    open fun showLoading(msg: String? = null) {
        ToastUtils.show("请求中")
    }

    /**
     * 隐藏 loading 提示
     */
    open fun dismissLoading(msg: String? = null) {
        ToastUtils.show(msg)
    }

    open fun isUsedLeanCloud(): Boolean {
        return true
    }

    /**
     * 通过反射创建 viewBinding
     * @return ViewBinding
     */
    @Suppress("UNCHECKED_CAST")
    open fun createViewBinding(): VB? {
        val actualGenericsClass = findActualGenericsClass<VB>(ViewBinding::class.java)
            ?: throw NullPointerException("Can not find a ViewBinding Generics in ${javaClass.simpleName}")
        var viewBinding: VB? = null
        try {
            val inflate =
                actualGenericsClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
            viewBinding = inflate.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return viewBinding
    }

    /**
     * 通过反射创建 viewModel
     * @return  ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    open fun createViewModel(): VM {
        val actualGenericsClass = findActualGenericsClass<VM>(BaseViewModel::class.java)
            ?: throw NullPointerException("Can not find a ViewModel Generics in ${javaClass.simpleName}")
        if (Modifier.isAbstract(actualGenericsClass.modifiers)) {
            throw IllegalStateException("$actualGenericsClass is an abstract class,abstract ViewModel class can not create a instance!")
        }
        return ViewModelProvider(this)[actualGenericsClass]
    }
}