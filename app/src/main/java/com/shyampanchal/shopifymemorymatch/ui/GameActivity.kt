package com.shyampanchal.shopifymemorymatch.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.shyampanchal.shopifymemorymatch.R
import com.shyampanchal.shopifymemorymatch.models.CardState
import com.shyampanchal.shopifymemorymatch.prefs.SharedPrefsManager
import com.shyampanchal.shopifymemorymatch.ui.adapters.CardAdapter
import com.shyampanchal.shopifymemorymatch.ui.listeners.CardClickListener
import com.shyampanchal.shopifymemorymatch.viewmodels.GameViewModel
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val vm: GameViewModel by lazy { ViewModelProviders.of(this).get(GameViewModel::class.java) }
    private val sharedPrefsManager: SharedPrefsManager by lazy { SharedPrefsManager(this) }
    private val numColumns = 4
    private val numRows = 6
    private var numMatches = 0
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setupViewModel()

        reset_button.setOnClickListener {
            vm.resetGame()
        }

        shuffle_button.setOnClickListener {
            vm.shuffleImages()
        }

        settings_button.setOnClickListener {
            val matchesOptions = arrayOf("2 matches", "3 matches", "4 matches")

            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setTitle("Select the Number of Matches")
                .setItems(matchesOptions) { _, option ->
                    when (option) {
                        0 -> sharedPrefsManager.setMatches(2)
                        1 -> sharedPrefsManager.setMatches(3)
                        2 -> sharedPrefsManager.setMatches(4)
                    }
                    vm.setup(this)
                    numMatches = sharedPrefsManager.readMatches()
                    setupRecyclerView(numMatches)
                }
            val optionsDialog = dialogBuilder.create()
            optionsDialog.show()
        }

        numMatches = sharedPrefsManager.readMatches()

        setupRecyclerView(numMatches)
    }

    private fun setupRecyclerView(matches: Int) {
        cards_list.layoutManager = GridLayoutManager(this, numColumns)
        cardAdapter = CardAdapter(object : CardClickListener {
            override fun onClick(cardIndex: Int, cardState: CardState) {
                if (cardState == CardState.OPEN) {
                    if (vm.selectedCards.size == matches - 1) {
                        cardAdapter.delayClick = true
                        val cardClickHandler = Handler()
                        cardClickHandler.postDelayed({
                            cardAdapter.delayClick = false
                            vm.selectCard(cardIndex)
                        }, 500)
                    } else {
                        vm.selectCard(cardIndex)
                    }
                }
            }
        })
        cards_list.adapter = cardAdapter
    }

    private fun setupViewModel() {
        vm.setup(this)

        vm.imagesData.observe(this, Observer { imagesList ->
            if (imagesList.isNotEmpty()) {
                cardAdapter.imagesList = imagesList
            }
        })

        vm.pairsData.observe(this, Observer { pairs ->
            pairs_textview.text = pairs.toString()

            val numProducts = (numRows * numColumns) / numMatches
            if (pairs == numProducts) {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("You won!")
                    .setMessage("Nice job! You've won with a score of ${vm.scoreData.value}!")
                    .setCancelable(false)
                    .setPositiveButton("Reset") { _, _ -> vm.resetGame() }
                val winDialog = dialogBuilder.create()
                winDialog.show()
            }
        })

        vm.scoreData.observe(this, Observer { score ->
            score_textview.text = score.toString()
        })
    }
}
