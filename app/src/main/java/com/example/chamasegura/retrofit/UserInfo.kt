package com.example.chamasegura.retrofit

class UserInfo private constructor() {
    var email: String? = null
    var idRole: Long? = null

    companion object {
        private var instance: UserInfo? = null

        fun getInstance(): UserInfo {
            if (instance == null) {
                instance = UserInfo()
            }
            return instance!!
        }
    }
}