package com.leftindust.mediq.helper.mocker

class EmailFaker(seed: Long) : MediqFaker<String>(seed) {
    private val domains =
        listOf("gmail.com", "yahoo.com", "icloud.com", "hotmail.com", "aol.com", "msn.com", "comcast.net")

    override fun create(): String {
        return "${faker.name.firstName()}.${faker.name.lastName()}@${domains[seededRandom.nextInt(domains.size)]}"
    }
}
