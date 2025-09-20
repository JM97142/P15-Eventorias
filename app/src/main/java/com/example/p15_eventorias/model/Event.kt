package com.example.p15_eventorias.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val address: String = "",
    val imageUrl: String? = null,
    val attachmentUrl: String? = null
)