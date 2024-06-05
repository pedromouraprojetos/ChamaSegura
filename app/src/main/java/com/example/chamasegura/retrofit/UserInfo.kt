package com.example.chamasegura.retrofit

class UserInfo private constructor() {
    var email: String? = null

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