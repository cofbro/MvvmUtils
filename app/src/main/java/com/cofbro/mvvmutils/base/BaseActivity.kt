package com.cofbro.mvvmutils.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cofbro.mvvmutils.lean.LeanCloudUtils
import com.hjq.toast.ToastUtils
import java.lang.reflect.Modifier


/**
 * 1.封装了一层 Activity，我们自己的 Activity 需要继承自 BaseActivity
 *
 * 2.继承之后的 Activity默认持有 ViewModel 和 ViewBinding
 *
 * 3.内部封装好了网络请求状态的变化 --> LoadingStatus，在onCreate中
 * 设置了观察者模式，使用者无需关心网络请求状态
 *
 *
 * @param VM ViewModel
 * @param VB ViewBinding
 *
 */
abstract class BaseActivity<VM : BaseViewModel<*>, VB : ViewBinding> : AppCompatActivity() {
    protected var mContext: Context? = null


    protected val viewModel by lazy {
        createViewModel()
    }

    protected val binding by lazy {
        createViewBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        mContext = this
        setContentView(binding?.root)
        viewModel.loadingDataState.observe(this) {
            when (it.state) {
                DataState.STATE_LOADING ->
                    showLoading()
                else ->
                    dismissLoading(it.msg)
            }
        }
        if (LeanCloudUtils.isUsed() && isUseLeanCloud()) {
            viewModel.leanCloudLiveData.observe(this) {
                when (it.state) {
                    DataState.STATE_LOADING -> {
                        showLoading()
                    }
                    DataState.STATE_SUCCESS -> {
                        dismissLoading(it.msg)
                    }
                    else ->
                        dismissLoading(it.msg)
                }
            }
        }
        onActivityCreated(savedInstanceState)
    }

    /**
     *  当onCreate方法执行完毕前就会执行此方法，相当于暴露给用户的<var>onCreate</var>
     *  @param  savedInstanceState savedInstanceState
     *  @see onCreate
     */
    abstract fun onActivityCreated(savedInstanceState: Bundle?)

    /**
     * 显示请求 loading 中的提示
     */
    open fun showLoading(msg: String? = "请求中") {
        ToastUtils.show(msg)
    }

    /**
     * 隐藏 loading 提示
     */
    open fun dismissLoading(msg: String? = "请求完毕") {
        ToastUtils.show(msg)
    }

    open fun isUseLeanCloud(): Boolean {
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
        //if (clsMap.containsKey(actualGenericsClass)) return clsMap[actualGenericsClass] as VM
        //clsMap[actualGenericsClass] = vb
        return ViewModelProvider(this)[actualGenericsClass]
    }

    /**
     * 设置导航栏字体颜色
     * @param window Window
     * @param light true: 黑色; false: 白色
     */
    fun setStatusBarTextColor(window: Window, light: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = if (light) {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.decorView.systemUiVisibility = systemUiVisibility
        }
    }
}