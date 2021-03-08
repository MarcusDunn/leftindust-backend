package com.leftindust.mediq.external.firebase

import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unwrap

@SpringBootTest
@Tag("firebase")
internal class UserFetcherTest(
    @Autowired private val userFetcher: UserFetcher,
) {

    @Test
    fun getUserInfo() {
        val result = runBlocking { userFetcher.getUserInfo("c9LCrndzlCah2gFlX6evBEPI4zp2", FakeAuth.Valid.Token) }

        assert(result.getOrNull()!!.email == "marcus.s.dunn@gmail.com")
    }

    @Test
    fun getUsers() {
        val result = runBlocking { userFetcher.getUsers(FakeAuth.Valid.Token) }

        assert(result.unwrap().any {it.email == "marcus.s.dunn@gmail.com"})
    }
}