package com.shyampanchal.shopifymemorymatch.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.shyampanchal.shopifymemorymatch.api.ProductsRepository
import com.shyampanchal.shopifymemorymatch.models.CardState
import com.shyampanchal.shopifymemorymatch.models.Product
import com.shyampanchal.shopifymemorymatch.prefs.SharedPrefsManager
import com.shyampanchal.shopifymemorymatch.viewmodels.base.BaseViewModel
import javax.inject.Inject

class GameViewModel : BaseViewModel() {

    @Inject lateinit var productsRepository: ProductsRepository

    private lateinit var context: Context
    private val sharedPrefsManager: SharedPrefsManager by lazy { SharedPrefsManager(context) }

    val productsData: MutableLiveData<MutableList<Product>> = MutableLiveData()
    val errorData: MutableLiveData<String> = MutableLiveData()
    val pairsData: MutableLiveData<Int> = MutableLiveData()
    val scoreData: MutableLiveData<Int> = MutableLiveData()
    val highScoreData = MutableLiveData<Int>()

    val selectedCards = mutableListOf<Int>()
    private val numRows = 6
    private val numColumns = 4
    var numMatches = 0
    set(value) {
        field = value
        sharedPrefsManager.setMatches(value)
    }

    fun setup(context: Context) {
        this.context = context

        pairsData.value = 0
        scoreData.value = 0

        highScoreData.value = sharedPrefsManager.readHighScore()

        numMatches = sharedPrefsManager.readMatches()
        val numProducts = (numRows * numColumns) / numMatches

        productsRepository.products.observeForever { productsList ->
            if (productsList.isEmpty()) {
                errorData.value = "Having trouble loading images. Please try again later."
            }
            productsData.value = productsList
        }
        fetchProducts(numProducts)
    }

    private fun fetchProducts(numProducts: Int) {
        productsRepository.getProducts(numProducts, numMatches)
    }

    fun refetchProducts() {
        val numProducts = (numRows * numColumns) / numMatches
        fetchProducts(numProducts)
    }

    fun shuffleProducts() {
        val productsList = productsData.value
        productsList?.shuffle()
        productsData.value = productsList
    }

    fun updateHighScore(): Boolean {
        if (scoreData.value!! < highScoreData.value!! || highScoreData.value!! == 0) {
            highScoreData.value = scoreData.value
            sharedPrefsManager.setHighScore(scoreData.value!!)
            return true
        }

        return false
    }

    fun resetGame() {
        val productsList = productsData.value
        productsList?.forEach { product ->
            product.cardState = CardState.CLOSED
        }

        productsList?.shuffle()
        productsData.value = productsList

        pairsData.value = 0
        scoreData.value = 0
        highScoreData.value = sharedPrefsManager.readHighScore()
    }

    fun selectCard(cardIndex: Int): Boolean {
        selectedCards.add(cardIndex)
        var cardsAreEqual = false

        if (selectedCards.size == numMatches) {
            val currentScore = scoreData.value
            scoreData.value = if (currentScore == null) 1 else (currentScore + 1)
            val productsList = productsData.value

            val firstCard = productsList?.get(selectedCards[0])
            cardsAreEqual = true
            selectedCards.subList(1, selectedCards.size).forEach {
                val product = productsList?.get(it)
                if (product?.title != firstCard?.title) {
                    cardsAreEqual = false
                }
            }
            selectedCards.forEach {
                val product = productsList?.get(it)
                product?.cardState = if (cardsAreEqual) CardState.MATCHED else CardState.CLOSED
            }

            if (cardsAreEqual) {
                val currentNumPairs = pairsData.value
                pairsData.value = if (currentNumPairs == null) 0 else (currentNumPairs + 1)
            }

            productsData.value = productsList
            selectedCards.clear()

        }

        return cardsAreEqual
    }

    override fun onCleared() {
        super.onCleared()
        productsRepository.onClear()
    }
}