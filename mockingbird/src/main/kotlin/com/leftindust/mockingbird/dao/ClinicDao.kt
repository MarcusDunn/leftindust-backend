package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput

interface ReadClinic : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.READ to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions

    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Clinic>
    suspend fun getByCid(cid: GraphQLClinic.ID, requester: MediqToken): Clinic
}

interface CreateClinic : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.CREATE to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions
    suspend fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic
}

interface UpdateClinic : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.UPDATE to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions
    suspend fun editClinic(clinic: GraphQLClinicEditInput, requester: MediqToken): Clinic
}

interface DeleteClinic : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.DELETE to Tables.Clinic))
    }

    override fun necessaryPermissions() = UpdateClinic.necessaryPermissions
}

@Deprecated("Prefer smaller interfaces")
interface ClinicDao : ReadClinic, CreateClinic, UpdateClinic, DeleteClinic {
    override fun necessaryPermissions() = ReadClinic.necessaryPermissions +
            CreateClinic.necessaryPermissions +
            UpdateClinic.necessaryPermissions +
            DeleteClinic.necessaryPermissions
}
