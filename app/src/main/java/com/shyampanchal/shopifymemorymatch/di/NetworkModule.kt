package com.shyampanchal.shopifymemorymatch.di

import com.shyampanchal.shopifymemorymatch.api.ProductsApi
import com.shyampanchal.shopifymemorymatch.api.ProductsRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Reusable
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)

        return Retrofit.Builder()
            .baseUrl("https://shopicruit.myshopify.com/admin/")
            .client(client.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    @Reusable
    fun provideProductsApi(retrofit: Retrofit) = retrofit.create(ProductsApi::class.java)

    @Provides
    @Reusable
    fun provideProductsRepository(api: ProductsApi) = ProductsRepository(api)
}