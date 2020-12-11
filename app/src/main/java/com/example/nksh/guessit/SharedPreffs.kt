package com.example.nksh.guessit

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.res.TypedArrayUtils.getText
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

fun setSharedPrefs(
    prefsEditor: KFunction2<@ParameterName(name = "name") String, @ParameterName(name = "mode") Int, SharedPreferences>,
    textFromStringTable: KFunction1<@ParameterName(name = "resId") Int, CharSequence>
) {
    val editor = prefsEditor(textFromStringTable(R.string.sp_key).toString(), Context.MODE_PRIVATE).edit()
    editor.putBoolean(textFromStringTable(R.string.sp_sound).toString(),MainActivity.sound)
    editor.putBoolean(textFromStringTable(R.string.sp_music).toString(),MainActivity.music)
    editor.putInt(textFromStringTable(R.string.sp_wins).toString(),MainActivity.wins)
    editor.putInt(textFromStringTable(R.string.sp_losses).toString(),MainActivity.losses)
    editor.putInt(textFromStringTable(R.string.sp_ties).toString(),MainActivity.ties)
    editor.putInt(textFromStringTable(R.string.sp_games_played).toString(),MainActivity.gamesPlayed)
    editor.putInt(textFromStringTable(R.string.sp_time).toString(),MainActivity.time)
    editor.putInt(textFromStringTable(R.string.sp_amount_of_rounds).toString(),MainActivity.amountOfRounds)
    editor.putInt(textFromStringTable(R.string.sp_current_amount_of_rounds).toString(),MainActivity.currentAmountOfRounds)
    editor.apply()
}

fun loadSharedPrefs(
    prefsEditor: KFunction2<@ParameterName(name = "name") String, @ParameterName(name = "mode") Int, SharedPreferences>,
    textFromStringTable: KFunction1<@ParameterName(name = "resId") Int, CharSequence>
) {
    val editor = prefsEditor(textFromStringTable(R.string.sp_key).toString(), Context.MODE_PRIVATE)
    MainActivity.sound = editor.getBoolean(textFromStringTable(R.string.sp_sound).toString(), true)
    MainActivity.music = editor.getBoolean(textFromStringTable(R.string.sp_music).toString(), true)
    MainActivity.wins = editor.getInt(textFromStringTable(R.string.sp_wins).toString(), 0)
    MainActivity.losses = editor.getInt(textFromStringTable(R.string.sp_losses).toString(),0)
    MainActivity.ties = editor.getInt(textFromStringTable(R.string.sp_ties).toString(),0)
    MainActivity.gamesPlayed = editor.getInt(textFromStringTable(R.string.sp_games_played).toString(),0)
    MainActivity.time = editor.getInt(textFromStringTable(R.string.sp_time).toString(),0)
    MainActivity.amountOfRounds = editor.getInt(textFromStringTable(R.string.sp_amount_of_rounds).toString(),0)
    MainActivity.currentAmountOfRounds = editor.getInt(textFromStringTable(R.string.sp_current_amount_of_rounds).toString(),0)
}
