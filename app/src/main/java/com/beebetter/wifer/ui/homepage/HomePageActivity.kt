package com.beebetter.wifer.ui.homepage

import androidx.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import com.beebetter.base.view.BaseActivity
import com.beebetter.wifer.R
import kotlin.reflect.KClass

class HomePageActivity : BaseActivity<com.beebetter.wifer.databinding.ActivityHomepageBinding,HomePageVM>() {
    override val layoutId = R.layout.activity_homepage
    override val viewModelClass = HomePageVM::class
       
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
    }
}
