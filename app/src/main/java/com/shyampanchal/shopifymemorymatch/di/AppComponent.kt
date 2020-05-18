package com.shyampanchal.shopifymemorymatch.di

import com.shyampanchal.shopifymemorymatch.ui.GameActivity
import dagger.Component

@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun inject(activity: GameActivity)
}