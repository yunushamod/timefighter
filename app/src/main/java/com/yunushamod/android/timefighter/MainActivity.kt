package com.yunushamod.android.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var gameScoreTextView : TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 60_000
    private var countDownInterval: Long = 1_000
    private val timeFighterViewModel: TimeFighterViewModel by lazy{
        ViewModelProvider(this)[TimeFighterViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)
        tapMeButton.setOnClickListener{
            val bounceAnimator = AnimationUtils.loadAnimation(this, R.anim.bounce)
            if(!timeFighterViewModel.gameStarted) startGame()
            it.startAnimation(bounceAnimator)
            incrementScore()
        }
        if(savedInstanceState == null) resetGame()
        else{
            timeFighterViewModel.score = savedInstanceState.getInt(SCORE_KEY, timeFighterViewModel.score)
            timeFighterViewModel.gameStarted = savedInstanceState.getBoolean(GAME_STARTED_KEY, timeFighterViewModel.gameStarted)
            timeFighterViewModel.timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY, timeFighterViewModel.timeLeft)
            restoreGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when{
            R.id.about_item == item.itemId -> {
                showInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TIME_LEFT_KEY, timeFighterViewModel.timeLeft)
        outState.putInt(SCORE_KEY, timeFighterViewModel.score)
        outState.putBoolean(GAME_STARTED_KEY, timeFighterViewModel.gameStarted)
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        AlertDialog.Builder(this).setTitle(dialogTitle).setMessage(dialogMessage)
            .show()
    }

    private fun incrementScore(){
        timeFighterViewModel.score++
        val newScore = getString(R.string.your_score, timeFighterViewModel.score)
        gameScoreTextView.text = newScore
    }

    private fun resetGame(){
        timeFighterViewModel.score = 0
        timeFighterViewModel.timeLeft = 60
        val initialScore = getString(R.string.your_score, timeFighterViewModel.score)
        val initialTime = getString(R.string.time_left, timeFighterViewModel.timeLeft)
        gameScoreTextView.text = initialScore
        timeLeftTextView.text = initialTime
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeFighterViewModel.timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_left, timeFighterViewModel.timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }
        timeFighterViewModel.gameStarted = false
    }

    private fun restoreGame(){
        val restoredScore = getString(R.string.your_score, timeFighterViewModel.score)
        gameScoreTextView.text = restoredScore
        val restoredTimeLeft = getString(R.string.time_left, timeFighterViewModel.timeLeft)
        timeLeftTextView.text = restoredTimeLeft
        countDownTimer = object : CountDownTimer((timeFighterViewModel.timeLeft * 1000).toLong(), countDownInterval){
            override fun onTick(p0: Long) {
                timeFighterViewModel.timeLeft = p0.toInt() / 1000
                timeLeftTextView.text = getString(R.string.your_score, timeFighterViewModel.timeLeft)
            }
            override fun onFinish() = endGame()
        }
        countDownTimer.start()
        timeFighterViewModel.gameStarted = true
    }

    private fun startGame(){
        countDownTimer.start()
        timeFighterViewModel.gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.game_over_message, timeFighterViewModel.score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    companion object{
        private const val TIME_LEFT_KEY = "time_left"
        private const val SCORE_KEY = "score_key"
        private const val GAME_STARTED_KEY = "game_started"
    }
}