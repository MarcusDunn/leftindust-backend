package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.github.wnameless.json.flattener.JsonFlattener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import org.springframework.stereotype.Component

@Component
class ConverterQuery : Query {
    enum class ConvertTarget {
        Json,
        Csv,
    }

    fun convert(json: String, target: ConvertTarget, authContext: GraphQLAuthContext): String {
        val asJsonArray = JsonParser.parseString(json).asJsonArray
        return when (target) {
            ConvertTarget.Json -> {
                asJsonArray.toString()
            }
            ConvertTarget.Csv -> {
                var lastKeySet: Set<String>? = null
                val rows = emptyList<JsonObject>().toMutableList()
                for (jsonElement in asJsonArray) {
                    val flattened = JsonFlattener.flatten(jsonElement.toString())
                    val flattenedJsonObject = JsonParser.parseString(flattened).asJsonObject
                    if (lastKeySet == null || lastKeySet == flattenedJsonObject.keySet()) {
                        lastKeySet = flattenedJsonObject.keySet()
                        rows.add(flattenedJsonObject)
                    } else {
                        throw GraphQLKotlinException("invalid input json, all elements of the array must be equal")
                    }
                }
                StringBuilder().apply {
                    for (key in lastKeySet!!) {
                        append(key)
                        append(",")
                    }
                    append("\n")
                    for (row in rows) {
                        for (key in lastKeySet) {
                            append(row[key])
                            append(",")
                        }
                        append("\n")
                    }
                }.toString()
            }
        }
    }
}