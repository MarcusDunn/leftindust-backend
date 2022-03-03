package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput

interface ReadClinicDao : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.READ to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions

    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Clinic>
    suspend fun getByCid(cid: GraphQLClinic.ID, requester: MediqToken): Clinic
}

interface CreateClinicDao : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.CREATE to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions
    suspend fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic
}

interface UpdateClinicDao : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.UPDATE to Tables.Clinic))
    }

    override fun necessaryPermissions() = necessaryPermissions
    suspend fun editClinic(clinic: GraphQLClinicEditInput, requester: MediqToken): Clinic
}

interface DeleteClinicDao : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.DELETE to Tables.Clinic))
    }

    override fun necessaryPermissions() = UpdateClinicDao.necessaryPermissions
}

@Deprecated("Prefer smaller interfaces")
interface ClinicDao : ReadClinicDao, CreateClinicDao, UpdateClinicDao, DeleteClinicDao {
    override fun necessaryPermissions() = ReadClinicDao.necessaryPermissions +
            CreateClinicDao.necessaryPermissions +
            UpdateClinicDao.necessaryPermissions +
            DeleteClinicDao.necessaryPermissions
}
