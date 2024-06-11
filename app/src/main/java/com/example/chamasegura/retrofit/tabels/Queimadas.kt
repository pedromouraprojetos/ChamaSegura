package com.example.chamasegura.retrofit.tabels

data class Queimadas(
    val location: Long,
    val type: Long,
    val date: String,
    val reason: String,
    val status: String,
    val idUser: Long
)
