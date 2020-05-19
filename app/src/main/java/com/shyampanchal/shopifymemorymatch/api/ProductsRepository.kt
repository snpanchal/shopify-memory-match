package com.shyampanchal.shopifymemorymatch.api

import androidx.lifecycle.MutableLiveData
import com.shyampanchal.shopifymemorymatch.models.Product
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

private const val API_TOKEN = "c32313df0d0ef512ca64d5b336a0d7c6"

class ProductsRepository(private val api: ProductsApi) {

    val products: MutableLiveData<MutableList<Product>> = MutableLiveData()
    private var disposable: Disposable = Disposables.empty()

    fun getProducts(numProducts: Int, numMatches: Int) {
        disposable = api.getProducts(1, API_TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->

                val productsList = list.productsList
                val productsAmt = if (productsList.size < numProducts) productsList.size else numProducts
                val uniqueProducts = mutableListOf<Product>()
                for (i in 0 until productsAmt) {
                    uniqueProducts.add(productsList[i])
                }

                val duplicatedCards = mutableListOf<Product>()
                for (i in 0 until numMatches) {
                    val duplicateProducts = uniqueProducts.map { it.copy() }
                    duplicatedCards.addAll(duplicateProducts)
                }

                duplicatedCards.shuffle()

                products.value = duplicatedCards

            }, {
                products.value = mutableListOf()
            })
    }

    fun onClear() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

}