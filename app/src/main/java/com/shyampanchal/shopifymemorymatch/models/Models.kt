package com.shyampanchal.shopifymemorymatch.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: ProductImage
)

@JsonClass(generateAdapter = true)
data class ProductImage(
    @Json(name = "id") val id: String,
    @Json(name = "src") val link: String,
    var cardState: CardState = CardState.CLOSED
)

@JsonClass(generateAdapter = true)
data class ProductsList(
    @Json(name = "products") val productsList: List<Product>
)

enum class CardState {
    OPEN,
    CLOSED,
    MATCHED
}
