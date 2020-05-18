package com.shyampanchal.shopifymemorymatch.api

import androidx.lifecycle.MutableLiveData
import com.shyampanchal.shopifymemorymatch.models.ProductImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

private const val API_TOKEN = "c32313df0d0ef512ca64d5b336a0d7c6"

class ProductsRepository(private val api: ProductsApi) {

    val productImages: MutableLiveData<MutableList<ProductImage>> = MutableLiveData()
    private var disposable: Disposable = Disposables.empty()

    fun getProducts(numProducts: Int, numMatches: Int) {
        disposable = api.getProducts(1, API_TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                val productsList = it.productsList
                val productsAmt = if (productsList.size < numProducts) productsList.size else numProducts
                val uniqueImagesList = mutableListOf<ProductImage>()
                for (i in 0 until productsAmt) {
                    val product = productsList[i]
                    uniqueImagesList.add(product.image)
                }

                val duplicatedCards = mutableListOf<ProductImage>()
                for (i in 0 until numMatches) {
                    duplicatedCards.addAll(uniqueImagesList)
                }

                duplicatedCards.shuffle()

                productImages.value = duplicatedCards

            }, {
                productImages.value = mutableListOf()
            })
    }

    fun onClear() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

}