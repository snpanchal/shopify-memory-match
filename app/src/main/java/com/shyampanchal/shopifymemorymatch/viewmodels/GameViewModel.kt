package com.shyampanchal.shopifymemorymatch.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.shyampanchal.shopifymemorymatch.api.ProductsRepository
import com.shyampanchal.shopifymemorymatch.models.CardState
import com.shyampanchal.shopifymemorymatch.models.ProductImage
import com.shyampanchal.shopifymemorymatch.prefs.SharedPrefsManager
import com.shyampanchal.shopifymemorymatch.viewmodels.base.BaseViewModel
import javax.inject.Inject

class GameViewModel : BaseViewModel() {

    @Inject lateinit var productsRepository: ProductsRepository

    lateinit var context: Context
    private val sharedPrefsManager: SharedPrefsManager by lazy { SharedPrefsManager(context) }

    val imagesData: MutableLiveData<MutableList<ProductImage>> = MutableLiveData()
    val errorData: MutableLiveData<String> = MutableLiveData()
    val pairsData: MutableLiveData<Int> = MutableLiveData()
    val scoreData: MutableLiveData<Int> = MutableLiveData()

    private var numMatches = 0
    val selectedCards = mutableListOf<Int>()

    fun setup(context: Context) {
        this.context = context

        pairsData.value = 0
        scoreData.value = 0

        val numRows = 6
        val numColumns = 4
        numMatches = sharedPrefsManager.readMatches()
        val numProducts = (numRows * numColumns) / numMatches
        fetchProducts(numProducts)
    }

    private fun fetchProducts(numProducts: Int) {
        productsRepository.productImages.observeForever { productImagesList ->
            if (productImagesList.isEmpty()) {
                errorData.value = "Have trouble loading images. Please try again later."
            }
            imagesData.value = productImagesList
        }

        productsRepository.getProducts(numProducts, numMatches)
    }

    fun shuffleImages() {
        val imagesList = imagesData.value
        imagesList?.shuffle()
        imagesData.value = imagesList
    }

    fun resetGame() {
        val imagesList = imagesData.value
        imagesList?.forEach { image ->
            image.cardState = CardState.CLOSED
        }

        imagesList?.shuffle()
        imagesData.value = imagesList

        pairsData.value = 0
        scoreData.value = 0
    }

    fun selectCard(cardIndex: Int) {
        selectedCards.add(cardIndex)

        if (selectedCards.size == numMatches) {
            val currentScore = scoreData.value
            scoreData.value = if (currentScore == null) 1 else (currentScore + 1)
            val imagesList = imagesData.value

            val firstCard = imagesList?.get(selectedCards[0])
            var cardsAreEqual = true
            selectedCards.subList(1, selectedCards.size).forEach {
                val image = imagesList?.get(it)
                if (image?.id != firstCard?.id) {
                    cardsAreEqual = false
                }
            }
            selectedCards.forEach {
                val image = imagesList?.get(it)
                image?.cardState = if (cardsAreEqual) CardState.MATCHED else CardState.CLOSED
            }

            if (cardsAreEqual) {
                val currentNumPairs = pairsData.value
                pairsData.value = if (currentNumPairs == null) 0 else (currentNumPairs + 1)
            }

            imagesData.value = imagesList
            selectedCards.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        productsRepository.onClear()
    }
}