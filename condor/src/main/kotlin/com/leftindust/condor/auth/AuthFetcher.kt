package com.leftindust.condor.auth

interface AuthFetcher {
    fun token(): String
}