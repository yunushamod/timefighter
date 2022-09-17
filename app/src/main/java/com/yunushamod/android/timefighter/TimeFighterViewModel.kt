package com.yunushamod.android.timefighter

import androidx.lifecycle.ViewModel

class TimeFighterViewModel : ViewModel() {
    var score: Int = 0
    var timeLeft: Int = 60
    var gameStarted: Boolean = false
}