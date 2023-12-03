package com.example.mindboost.dataclasses


data class BecksTestDetail(
    val date: String,
    val score: Int,
    val questionsAndAnswers: List<Pair<String, String>> // Nowe pole
)
