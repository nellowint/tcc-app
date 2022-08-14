package br.com.vieirateam.tcc.model

import java.io.Serializable

data class Question(
        var id: Long = 0,
        var name: String = "") : Serializable