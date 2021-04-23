package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName

@GraphQLName("ReleaseIdInput")
@GraphQLDescription("release ids for icd-11. See https://icd.who.int/icdapi/docs2/SupportedClassifications/")
enum class GraphQLReleaseIdInput {
    // supported for icd 11
    R_2020_09,
    R_2019_04,
    R_2018;

    override fun toString(): String {
        return when (this) {
            R_2018 -> "2018"
            R_2019_04 -> "2019-04"
            R_2020_09 -> "2020-09"
        }
    }

    companion object {
        fun fromString(string: String): GraphQLReleaseIdInput {
            return when (string) {
                "2018" -> R_2018
                "2019-04" -> R_2019_04
                "2020-09" -> R_2020_09
                else -> throw IllegalArgumentException("cannot match string to ReleaseIdInput")
            }
        }
    }
}