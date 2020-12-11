package com.example.nksh.guessit


fun GetAnswers() :ArrayList<ArrayList<String>> {
    var answers = java.util.ArrayList<java.util.ArrayList<String>>()

    answers.add(arrayListOf("flower"))
    answers.add(arrayListOf("house", "building", "cabin"))

    return answers
}

