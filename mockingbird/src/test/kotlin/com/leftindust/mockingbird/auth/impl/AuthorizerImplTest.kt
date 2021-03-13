package com.leftindust.mockingbird.auth.impl

import com.leftindust.mockingbird.auth.Crud.READ
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.Tables.Patient
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.Success
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class AuthorizerImplTest {

    @MockK
    private lateinit var authorizationDao: AuthorizationDao

    @Test
    fun getAuthorization() {
        val readToPatients = mockk<Action> {
            every { referencedTableName } returns Patient
            every { permissionType } returns READ
        }

        coEvery { authorizationDao.getRolesForUserByUid("marcus") } returns Success(listOf(
            mockk {
                every { readToPatients } returns readToPatients
            }
        ))

        val actual = runBlocking {
            AuthorizerImpl(authorizationDao).getAuthorization(readToPatients, mockk {
                every { uid } returns "marcus"
            })
        }

        assertEquals(Authorization.Allowed, actual)

        coVerify { authorizationDao.getRolesForUserByUid("marcus") }
    }
}