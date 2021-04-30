package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

object GraphQLCanadianProvince : GraphQLProvince {
    enum class Provinces {
        BritishColumbia,
        Alberta,
        Saskatchewan,
        Manitoba,
        Ontario,
        Quebec,
        NewfoundlandAndLabrador,
        NewBrunswick,
        NovaScotia,
        PrinceEdwardIsland,
        Yukon,
        NorthwestTerritories,
        Nunavut;
    }

    enum class ProvinceShort {
        BC,
        AB,
        SK,
        MB,
        ON,
        QC,
        NL,
        NB,
        NS,
        PE,
        YT,
        NT,
        NU,
    }

    override fun asStrings() = Provinces.values().map { it.name }

    override fun asShortStrings() = ProvinceShort.values().map { it.name }

    override fun longToShort(province: String): String {
        return if (ProvinceShort.values().map { it.name }.contains(province)) {
            province
        } else {
            ProvinceShort.values()[Provinces.valueOf(province).ordinal].name
        }
    }

    override fun shortToLong(province: String): String {
        return if (Provinces.values().map { it.name }.contains(province)) {
            province
        } else {
            Provinces.values()[ProvinceShort.valueOf(province).ordinal].name
        }
    }
}

interface GraphQLProvince {
    fun asStrings(): List<String>
    fun asShortStrings(): List<String>

    @GraphQLIgnore
    fun longToShort(province: String): String

    @GraphQLIgnore
    fun shortToLong(province: String): String
}
