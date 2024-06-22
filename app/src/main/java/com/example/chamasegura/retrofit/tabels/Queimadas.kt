package com.example.chamasegura.retrofit.tabels

data class Queimadas(
    val idQueimada: Long?,
    val location: Long,
    val idTypeQueimadas: Long,
    val date: String,
    val reason: String,
    val status: String,
    val idUser: Long,
    val idAprovation: Long?,
    val idMunicipalities: String?
)
