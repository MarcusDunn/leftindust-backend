package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput


@GraphQLName("EventInput")
data class GraphQLEventInput(
    val title: String,
    val description: String? = null,
    @GraphQLDescription("UTC")
    val start: GraphQLTimeInput,
    @GraphQLDescription("UTC only can be unset / set to null if allDay is true")
    val end: GraphQLTimeInput? = null,
    @GraphQLDescription("defaults to false even if explicitly passed null")
    val allDay: Boolean? = false,
    val doctors: List<ID>? = emptyList(),
    val patients: List<ID>? = emptyList(),
    val recurrence: GraphQLRecurrenceInput? = null
)

@GraphQLName("EventEditInput")
@GraphQLDescription(
    """
    passing explicit nulls will unset the value (provided that leaves the event in a valid state) and not setting a value
     will leave it unchanged. The same consistency rules around start/end/allDay still apply and it is the callers 
     responsibility to pass a valid series of arguments 
"""
)
data class GraphQLEventEditInput(
    @GraphQLDescription("the id of the event you are looking to edit")
    val eid: ID,
    @GraphQLDescription("The new title of the event. Cannot be set to null")
    val title: String? = null,
    @GraphQLDescription("The new description. Can be set to null to remove the old description")
    val description: OptionalInput<String?> = OptionalInput.Undefined,
    @GraphQLDescription("The new start of the event. Cannot be null")
    val start: GraphQLTimeInput? = null,
    @GraphQLDescription("The new end of the event. Can only be null if the event is allDay")
    val end: OptionalInput<GraphQLTimeInput?> = OptionalInput.Undefined,
    @GraphQLDescription(" weather the event is all day or not. Cannot be null and must be false if and only if end is not null")
    val allDay: Boolean? = null,
    @GraphQLDescription("the doctors attached to this event. if set to null, no changes will be made, to clear the list you must pass an empty list")
    val doctors: List<ID>? = null,
    @GraphQLDescription("the patients attached to this event. see doctors for nullability rules")
    val patients: List<ID>? = null,
    @GraphQLDescription("the rules surrounding recurrence of the event, Can be set to null. Setting this value will overwrite the whole recurrence object, leaving fields blank will __not__ retain prior values")
    val recurrence: OptionalInput<GraphQLRecurrenceInput> = OptionalInput.Undefined
)


@GraphQLName("RecurrenceEditSettings")
@GraphQLDescription(
    """the date range that the edits will effect the reoccurring event. This allows things such as editing a 
            single event of a reoccurring event or leaving the past events untouched but editing future ones"""
)
data class GraphQLRecurrenceEditSettings(
    @GraphQLDescription("the start of events that the edit should take place on")
    val editStart: GraphQLDateInput,
    @GraphQLDescription("the end of the events that the edit should take place on")
    val editEnd: GraphQLDateInput,
)