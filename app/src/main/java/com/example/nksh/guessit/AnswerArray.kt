package com.example.nksh.guessit

fun GetCategories() : ArrayList<Int> {
    return arrayListOf<Int>(
        R.drawable.flower_animation,
        R.drawable.house_animation,
        R.drawable.tv_animation,
        R.drawable.headphones_animation,
        R.drawable.phone_animation,
        R.drawable.boat_animation,
        R.drawable.sunglasses_animation,
        R.drawable.lamp_animation,
        R.drawable.bus_animation
    )
}
fun GetAnswers() :ArrayList<ArrayList<String>> {
    var answers = java.util.ArrayList<java.util.ArrayList<String>>()

    answers.add(arrayListOf("flower"))
    answers.add(arrayListOf("house", "building", "cabin"))
    answers.add(arrayListOf("tv", "television", "t.v."))
    answers.add(arrayListOf("headphones", "earphones"))
    answers.add(arrayListOf("smartphone", "cellphone", "phone", "telephone", "cell", "mobile phone"))
    answers.add(arrayListOf("sailboat", "boat", "ship"))
    answers.add(arrayListOf("sunglasses", "shades"))
    answers.add(arrayListOf("lamp", "light"))
    answers.add(arrayListOf("schoolbus", "bus"))

    return answers
}

