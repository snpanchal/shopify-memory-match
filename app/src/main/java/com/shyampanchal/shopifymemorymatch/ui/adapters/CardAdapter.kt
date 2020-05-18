package com.shyampanchal.shopifymemorymatch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shyampanchal.shopifymemorymatch.R
import com.shyampanchal.shopifymemorymatch.models.CardState
import com.shyampanchal.shopifymemorymatch.models.ProductImage
import com.shyampanchal.shopifymemorymatch.ui.listeners.CardClickListener
import kotlinx.android.synthetic.main.card_view.view.*

class CardAdapter(val cardClickListener: CardClickListener) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var delayClick: Boolean = false
    var imagesList = emptyList<ProductImage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount() = imagesList.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val image = imagesList[position]
        Glide.with(holder.itemView.context).load(image.link).into(holder.itemView.image)
        holder.updateCardState(image.cardState)
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var cardState: CardState = CardState.CLOSED

        init {
            view.image_cover.setOnClickListener {
                if (delayClick || cardState == CardState.MATCHED) {
                    return@setOnClickListener
                }

                updateCardState(CardState.OPEN)
                val adapterPosition = adapterPosition
                if (adapterPosition != -1) {
                    cardClickListener.onClick(adapterPosition, cardState)
                }
            }
        }

        fun updateCardState(cardState: CardState) {
            when (cardState) {
                CardState.OPEN -> {
                    itemView.image_cover.visibility = View.GONE
                    itemView.image.visibility = View.VISIBLE
                    this.cardState = CardState.OPEN
                }
                CardState.CLOSED -> {
                    itemView.image_cover.visibility = View.VISIBLE
                    itemView.image.visibility = View.GONE
                    this.cardState = CardState.CLOSED
                }
                CardState.MATCHED -> {
                    itemView.image_cover.visibility = View.GONE
                    itemView.image.visibility = View.VISIBLE
                    this.cardState = CardState.MATCHED
                }
            }
        }
    }

}