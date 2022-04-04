package com.leftindust.mockingbird.dao.email.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.entity.Email
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.util.makeUUID
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReadEmailDaoImplTest {
    @Nested
    inner class Authenticated {
        val authorizer = mockk<Authorizer>() {
            coEvery { getAuthorization(any(), any()) } returns Authorization.Allowed
        }

        @Test
        fun `check getDoctorEmails`() = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                emergencyContactRepository = mockk(),
                patientRepository = mockk(),
                authorizer = authorizer
            )
            val result = readEmailDaoImpl.getDoctorEmails(GraphQLDoctor.ID(makeUUID("getDoctorEmails")), mockk())
            assertEquals(emptyList<Email>(), result)
        }

        @Test
        fun `check getEmergencyContactEmails`() = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk(),
                emergencyContactRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                patientRepository = mockk(),
                authorizer = authorizer
            )
            val result = readEmailDaoImpl.getEmergencyContactEmails(
                GraphQLEmergencyContact.ID(makeUUID("getEmergencyContactEmails")),
                mockk()
            )
            assertEquals(emptyList<Email>(), result)
        }

        @Test
        fun `check getPatientEmails`() = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk(),
                emergencyContactRepository = mockk(),
                patientRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                authorizer = authorizer
            )
            val result =
                readEmailDaoImpl.getPatientEmails(GraphQLPatient.ID(makeUUID("getEmergencyContactEmails")), mockk())
            assertEquals(emptyList<Email>(), result)
        }
    }

    @Nested
    inner class Unauthenticated {
        val authorizer = mockk<Authorizer>() {
            coEvery { getAuthorization(any(), any()) } returns Authorization.Denied
        }

        @Test
        fun `check getDoctorEmails`(): Unit = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                emergencyContactRepository = mockk(),
                patientRepository = mockk(),
                authorizer = authorizer
            )
            assertThrows<NotAuthorizedException> {
                readEmailDaoImpl.getDoctorEmails(
                    GraphQLDoctor.ID(makeUUID("getDoctorEmails")),
                    mockk()
                )
            }
        }

        @Test
        fun `check getEmergencyContactEmails`(): Unit = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk(),
                emergencyContactRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                patientRepository = mockk(),
                authorizer = authorizer
            )
            assertThrows<NotAuthorizedException> {
                readEmailDaoImpl.getEmergencyContactEmails(
                    GraphQLEmergencyContact.ID(makeUUID("getEmergencyContactEmails")),
                    mockk()
                )
            }
        }

        @Test
        fun `check getPatientEmails`(): Unit = runBlocking {
            val readEmailDaoImpl = ReadEmailDaoImpl(
                doctorRepository = mockk(),
                emergencyContactRepository = mockk(),
                patientRepository = mockk {
                    every { getById(any()) } returns mockk {
                        every { email } returns mutableSetOf()
                    }
                },
                authorizer = authorizer
            )
            assertThrows<NotAuthorizedException> {
                readEmailDaoImpl.getPatientEmails(
                    GraphQLPatient.ID(makeUUID("getEmergencyContactEmails")),
                    mockk()
                )
            }
        }
    }
}