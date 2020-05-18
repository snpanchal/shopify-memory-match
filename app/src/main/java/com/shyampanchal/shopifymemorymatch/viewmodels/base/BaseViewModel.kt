package com.shyampanchal.shopifymemorymatch.viewmodels.base

import androidx.lifecycle.ViewModel
import com.shyampanchal.shopifymemorymatch.di.DaggerViewModelInjector
import com.shyampanchal.shopifymemorymatch.di.NetworkModule
import com.shyampanchal.shopifymemorymatch.di.ViewModelInjector
import com.shyampanchal.shopifymemorymatch.viewmodels.GameViewModel

abstract class BaseViewModel : ViewModel() {

    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject() {
        when (this) {
            is GameViewModel -> injector.inject(this)
        }
    }
}