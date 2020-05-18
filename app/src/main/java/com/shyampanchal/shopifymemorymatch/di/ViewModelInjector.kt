package com.shyampanchal.shopifymemorymatch.di

import com.shyampanchal.shopifymemorymatch.viewmodels.GameViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ViewModelInjector {

    fun inject(gameViewModel: GameViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}