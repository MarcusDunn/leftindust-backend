package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.CreateFormDao
import com.leftindust.mockingbird.dao.DeleteFormDao
import com.leftindust.mockingbird.dao.ReadFormDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.AssignedForm
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.impl.repository.HibernateAssignedFormRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
class FormDaoImpl(
    @Autowired private val formRepository: HibernateFormRepository,
    @Autowired private val assignedFormRepository: HibernateAssignedFormRepository,
    @Autowired authorizer: Authorizer
) : ReadFormDao, DeleteFormDao, CreateFormDao, AbstractHibernateDao(authorizer) {
    companion object {
        private val readForms = Crud.READ to Tables.Form
        private val createForms = Crud.CREATE to Tables.Form
        private val deleteForms = Crud.DELETE to Tables.Form
    }

    override fun necessaryPermissions() =
        ReadFormDao.necessaryPermissions +
                DeleteFormDao.necessaryPermissions +
                CreateFormDao.necessaryPermissions

    override suspend fun getByIds(ids: List<GraphQLFormTemplate.ID>, requester: MediqToken): Collection<Form> =
        if (requester can readForms) {
            withContext(Dispatchers.IO) {
                formRepository.getByIds(ids.map { it.id })
            }
        } else {
            throw NotAuthorizedException(requester, readForms)
        }

    override suspend fun getMany(range: GraphQLRangeInput, requester: MediqToken): List<Form> =
        if (requester can readForms) {
            withContext(Dispatchers.IO) {
                formRepository.findAll(range.toPageable()).toList()
            }
        } else {
            throw NotAuthorizedException(requester, readForms)
        }

    override suspend fun addForm(form: GraphQLFormTemplateInput, requester: MediqToken): Form =
        if (requester can createForms) {
            withContext(Dispatchers.IO) {
                formRepository.save(Form(form))
            }
        } else {
            throw NotAuthorizedException(requester, createForms)
        }

    override suspend fun deleteForm(form: GraphQLFormTemplate.ID, requester: MediqToken): Form =
        if (requester can deleteForms) {
            withContext(Dispatchers.IO) {
                val formEntity = formRepository.getById(form.id)
                formRepository.delete(formEntity)
                formEntity
            }
        } else {
            throw NotAuthorizedException(requester, deleteForms)
        }

    override suspend fun getByPatientAssigned(
        patient: GraphQLPatient.ID,
        requester: MediqToken
    ): Collection<AssignedForm> =
        if (requester can readForms) {
            withContext(Dispatchers.IO) {
                assignedFormRepository.findAllByPatient_Id(patient.id)
            }
        } else throw NotAuthorizedException(requester, readForms)
}