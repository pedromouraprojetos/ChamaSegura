package com.example.chamasegura.retrofit.tabels

data class Users(
    val idUsers: Long?,
    val email: String,
    val password: String,
    val FirstEnter: Boolean?,
    val name: String?,
    val idRole: Long?,
    var estado_conta: String,
    var idMunicipalities: Long?,
)
