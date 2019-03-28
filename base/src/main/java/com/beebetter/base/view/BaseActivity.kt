package com.beebetter.base.view

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.beebetter.base.viewmodel.BaseViewModel
import com.example.base.base.event.LiveEvent
import kotlin.reflect.KClass

abstract class BaseActivity<B: ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: B

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract val viewModelClass: KClass<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.setLifecycleOwner(this)
        initBinding(binding)
    }

    protected open fun initBinding(binding: B) {
        binding.setVariable(BR.vm, viewModel)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(viewModelClass!!.java)
    }

    protected fun <T : LiveEvent> subscribe(eventClass: KClass<T>, eventObserver: Observer<T>) {
        viewModel.subscribe(this, eventClass, eventObserver)
    }


    override public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}