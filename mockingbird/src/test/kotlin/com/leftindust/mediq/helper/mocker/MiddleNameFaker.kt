package com.leftindust.mediq.helper.mocker

class MiddleNameFaker(seed: Long) : MediqFaker<String>(seed) {

    override fun create(): String {
        return if (seededRandom.nextInt() > 50) {
            faker.dota.item()
        } else {
            faker.dota.hero()
        }
    }
}
