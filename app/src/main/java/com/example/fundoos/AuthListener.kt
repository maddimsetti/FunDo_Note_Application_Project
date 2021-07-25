package com.example.fundoos

interface AuthListener {
    fun onRegister(status: Boolean, exception: String?)
    fun onLogin(status: Boolean, exception: String?)
    fun onResetPassword(status: Boolean)
    fun onFacebookLogin(status: Boolean, exception: String?)
}