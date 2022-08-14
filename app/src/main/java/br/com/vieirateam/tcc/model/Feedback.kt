package br.com.vieirateam.tcc.model

data class Feedback(
        val token : String,
        val message: String?,
        val answers: List<Int>)