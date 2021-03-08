package com.leftindust.mediq.helper.mocker

class PhoneFaker(seed: Long) : MediqFaker<String>(seed) {

    override fun create(): String {
        return "(${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}) ${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}-${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}${seededRandom.nextInt(10)}"
    }
}
