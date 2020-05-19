package com.shyampanchal.shopifymemorymatch.ui

import android.app.Dialog
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.shyampanchal.shopifymemorymatch.R
import com.shyampanchal.shopifymemorymatch.models.CardState
import com.shyampanchal.shopifymemorymatch.ui.adapters.CardAdapter
import com.shyampanchal.shopifymemorymatch.ui.listeners.CardClickListener
import com.shyampanchal.shopifymemorymatch.viewmodels.GameViewModel
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.win_dialog_view.view.*

class GameActivity : AppCompatActivity() {

    private val vm: GameViewModel by lazy { ViewModelProviders.of(this).get(GameViewModel::class.java) }
    private val numColumns = 4
    private val numRows = 6
    private lateinit var cardAdapter: CardAdapter
    private val sounds: SoundPool by lazy { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var matchSound = 0
    private var winSound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setupViewModel()

        matchSound = sounds.load(this, R.raw.match_sound, 2)
        winSound = sounds.load(this, R.raw.game_win_sound, 1)

        reset_button.setOnClickListener {
            vm.resetGame()
        }

        shuffle_button.setOnClickListener {
            vm.shuffleProducts()
        }

        settings_button.setOnClickListener {
            val matchesOptions = arrayOf("2 matches", "3 matches", "4 matches")

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Select the Number of Matches")
                .setSingleChoiceItems(matchesOptions, vm.numMatches - 2) { dialog, option ->
                    vm.numMatches = option + 2
                    vm.refetchProducts()
                    setupRecyclerView(vm.numMatches)
                    dialog.dismiss()
                    val totalGroups = numRows * numColumns / vm.numMatches
                    pairs_textview.text = getString(R.string.groups_found, 0, totalGroups)
                }
            val optionsDialog = dialogBuilder.create()
            optionsDialog.show()
        }

        setupRecyclerView(vm.numMatches)
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
                            selectCard(cardIndex)
                        }, 500)
                    } else {
                        selectCard(cardIndex)
                    }
                }
            }
        })
        cards_list.adapter = cardAdapter
    }

    private fun selectCard(cardIndex: Int) {
        val matchSuccessful = vm.selectCard(cardIndex)
        if (matchSuccessful) {
            sounds.play(matchSound, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    private fun setupViewModel() {
        vm.setup(this)

        vm.productsData.observe(this, Observer { productsList ->
            if (productsList.isNotEmpty()) {
                cardAdapter.productsList = productsList
            }
        })

        vm.highScoreData.observe(this, Observer {
            high_score_textview.text = it.toString()
        })

        vm.errorData.observe(this, Observer { errorMsg ->
            val errorDialogBuilder = AlertDialog.Builder(this)
            errorDialogBuilder.setTitle("Something went wrong")
                .setMessage(errorMsg)
                .setPositiveButton("Refresh") { _, _ -> vm.refetchProducts() }
            val errorDialog = errorDialogBuilder.create()
            errorDialog.show()
        })

        vm.pairsData.observe(this, Observer { pairs ->
            val totalGroups = numRows * numColumns / vm.numMatches
            pairs_textview.text = getString(R.string.groups_found, pairs, totalGroups)

            val numProducts = (numRows * numColumns) / vm.numMatches
            if (pairs == numProducts) {
                val highScoreChanged = vm.updateHighScore()
                var winMessage = "Nice job! You've won in ${vm.scoreData.value} moves!"
                if (highScoreChanged) {
                    winMessage = "You've won in ${vm.scoreData.value} moves! That's a new high score!"
                }

                sounds.play(winSound, 1.0f, 1.0f, 0, 0, 1.0f)

                val winDialog = Dialog(this)
                winDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                winDialog.setCanceledOnTouchOutside(false)
                val winDialogView = LayoutInflater.from(this).inflate(R.layout.win_dialog_view, null)
                winDialogView.win_message.text = winMessage
                winDialogView.reset_button.setOnClickListener {
                    vm.resetGame()
                    winDialog.dismiss()
                }
                winDialog.setContentView(winDialogView)
                winDialog.show()
            }
        })

        vm.scoreData.observe(this, Observer { score ->
            score_textview.text = score.toString()
        })
    }
}
