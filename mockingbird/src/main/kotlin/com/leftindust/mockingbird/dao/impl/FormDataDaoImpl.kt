package com.leftindust.mockingbird.dao.impl

import com.google.gson.JsonElement
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.FormDataDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.FormData
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormDataRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class FormDataDaoImpl(
    @Autowired private val formDataRepository: HibernateFormDataRepository,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired authorizer: Authorizer
) : FormDataDao, AbstractHibernateDao(authorizer) {
    private companion object {
        val readForms = Crud.READ to Tables.Form
        val updateForms = Crud.UPDATE to Tables.Form
        val deleteForms = Crud.DELETE to Tables.Form
        val createForms = Crud.CREATE to Tables.Form
    }

    override suspend fun attachForm(patient: GraphQLPatient.ID, form: JsonElement, requester: MediqToken): FormData {
        val createFormsAndUpdatePatients = listOf(createForms, Crud.UPDATE to Tables.Patient)
        if (requester can createFormsAndUpdatePatients) {
            return formDataRepository.save(FormData(patient = patientRepository.getById(patient.id), data = form))
        } else {
            throw NotAuthorizedException(requester, createFormsAndUpdatePatients)
        }
    }

    override suspend fun getForms(patient: GraphQLPatient.ID, requester: MediqToken): List<FormData> {
        val readFormsAndPatient = listOf(readForms, Crud.READ to Tables.Patient)
        if (requester can readFormsAndPatient) {
            return formDataRepository.getByPatient_Id(patient.id)
        } else {
            throw NotAuthorizedException(requester, readFormsAndPatient)
        }
    }
}