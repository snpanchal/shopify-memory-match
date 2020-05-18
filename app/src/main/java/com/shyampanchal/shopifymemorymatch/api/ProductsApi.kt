package com.shyampanchal.shopifymemorymatch.api

import com.shyampanchal.shopifymemorymatch.models.ProductsList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ProductsApi {
    @GET("/admin/products.json")
    fun getProducts(
        @Query("page") pageNum: Int,
        @Query("access_token") token: String
    ): Observable<ProductsList>
}