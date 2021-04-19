package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@Transactional
@Repository
class PatientDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val doctorPatientRepository: HibernateDoctorPatientRepository,
    @Autowired private val visitRepository: HibernateVisitRepository,
    @Autowired private val sessionFactory: SessionFactory,
    @Autowired private val entityManager: EntityManager
) : PatientDao, AbstractHibernateDao(authorizer) {

    override suspend fun getByPID(pID: Long, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        val readToDatabase = Action(Crud.READ to Tables.Patient)
        return readToDatabase.getAuthorization(requester) {
            Success(
                patientRepository.getOneOrNull(pID)
                    ?: return@getAuthorization Failure(DoesNotExist())
            )
        }
    }

    override suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val permissions = listOf(
            Crud.CREATE to Tables.Patient,
            Crud.UPDATE to Tables.Doctor,
        ).map { Action(it) }
        return if (requester has permissions) {
            val newPatient = try {
                Patient(patient, sessionFactory.currentSession)
            } catch (e: IllegalArgumentException) {
                return Failure(InvalidArguments(e.message))
            }
            val savedPatient = patientRepository.save(newPatient)
            Success(savedPatient)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun removeByPID(pid: Long, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        val deletePatient = Action(Crud.DELETE to Tables.Patient)
        return deletePatient.getAuthorization(requester) {
            val toBeDeleted = patientRepository.getOneOrNull(pid) ?: return@getAuthorization Failure(DoesNotExist(""))
            patientRepository.delete(toBeDeleted)
            Success(toBeDeleted)
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): Collection<Patient> {
        if (requester can (Crud.READ to Tables.Patient)) {
            val doctor = doctorRepository.getOne(did)
            return doctorPatientRepository.getAllByDoctorId(doctor.id).map { it.patient }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun getByVisit(vid: Long, requester: MediqToken): Collection<Patient> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            visitRepository.getOne(vid).event.patients
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun addDoctorToPatient(
        patientInput: ID,
        doctorInput: ID,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val permissions = listOf(Crud.UPDATE to Tables.Doctor, Crud.UPDATE to Tables.Doctor).map { Action(it) }
        return if (requester has permissions) {
            val patient = patientRepository.getOneOrNull(patientInput.toLong())
                ?: return Failure(DoesNotExist("patient does not exist"))
            val doctor = doctorRepository.getOneOrNull(doctorInput.toLong())
                ?: return Failure(DoesNotExist("doctor does not exist"))
            return Success(patient.addDoctor(doctor))
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun searchByExample(
        example: GraphQLPatientExample,
        requester: MediqToken,
        strict: Boolean
    ): CustomResult<List<Patient>, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            searchByGqlExample(entityManager, example, strict)
        } else {
            Failure(NotAuthorized(requester, "cannot read to patient"))
        }
    }

    override suspend fun getMany(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField,
        requester: MediqToken
    ): Collection<Patient> {
        val size = to - from
        val page = to / size - 1
        return if (requester can (Crud.READ to Tables.Patient)) {
            patientRepository.findAll(PageRequest.of(page, size, Sort.by(sortedBy.fieldName))).toList()
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun update(
        patientInput: GraphQLPatientInput,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val updatePatients = Crud.UPDATE to Tables.Patient
        return if (requester can updatePatients) {
            val pid = patientInput.pid.getOrNull() ?: return Failure(InvalidArguments("pid must be defined"))
            val patient = patientRepository.getOneOrNull(pid.toLong())
                ?: return Failure(DoesNotExist("could not find the patient with pid ${pid.value}"))
            try {
                patient.setByGqlInput(patientInput, sessionFactory.currentSession)
                Success(patient)
            } catch (e: IllegalArgumentException) {
                Failure(InvalidArguments(e.message))
            }
        } else {
            Failure(NotAuthorized(requester, "not allowed to $updatePatients"))
        }
    }
}
