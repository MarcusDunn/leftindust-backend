package com.leftindust.mockingbird.graphql.types

object GraphQLCanadianProvince : GraphQLProvince {
    enum class Provinces {
        BritishColumbia,
        NorthwestTerritories,
        Yukon,
        Nunavut,
        Ontario,
        Quebec,
        NovaScotia,
        NewBrunswick,
        Manitoba,
        PrinceEdwardIsland,
        Saskatchewan,
        Alberta,
        NewfoundlandAndLabrador;
    }

    enum class ProvinceShort {
        AB,
        BC,
        MB,
        NB,
        NL,
        NT,
        NS,
        NU,
        ON,
        PE,
        QC,
        SK,
        YT,
    }

    override fun asStrings() = Provinces.values().map { it.name }

    override fun asShortStrings() = ProvinceShort.values().map { it.name }
}

interface GraphQLProvince {
    fun asStrings(): List<String>
    fun asShortStrings(): List<String>
}
