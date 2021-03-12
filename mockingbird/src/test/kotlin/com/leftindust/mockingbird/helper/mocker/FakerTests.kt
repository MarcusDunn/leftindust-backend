package com.leftindust.mockingbird.helper.mocker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class FakerTests {

    @Test
    internal fun `test create patient`() {
        val faker1 = PatientFaker(100)
        val faker2 = PatientFaker(100)

        val patient1 = faker1()
        val patient2 = faker2()
        assertEquals(patient1, patient2)
    }

    @Test
    internal fun `test create patient with same faker`() {
        val faker1 = PatientFaker(100)

        val patient1 = faker1()
        val patient2 = faker1()
        assertNotEquals(patient1, patient2)
    }

    @Test
    internal fun `test create group with same faker`() {
        val faker = GroupFaker(100)

        val group1 = faker()
        val group2 = faker()
        assertNotEquals(group1,  group2)
    }
}