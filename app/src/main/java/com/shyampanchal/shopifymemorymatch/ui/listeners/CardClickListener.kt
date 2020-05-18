package com.shyampanchal.shopifymemorymatch.ui.listeners

import com.shyampanchal.shopifymemorymatch.models.CardState

interface CardClickListener {
    fun onClick(cardIndex: Int, cardState: CardState)
}