package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.*
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


@Transactional
@Repository
class PatientDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val doctorPatientRepository: HibernateDoctorPatientRepository,
    @Autowired private val visitRepository: HibernateVisitRepository,
    @Autowired private val sessionFactory: SessionFactory,
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
        patient: Patient,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val writeToPatient = Action(Crud.CREATE to Tables.Patient)
        return writeToPatient.getAuthorization(requester) {
            Success(patientRepository.save(patient))
        }
    }

    override suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        doctorIds: List<ID>,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val permissions = listOf(
            Crud.CREATE to Tables.Patient,
            Crud.UPDATE to Tables.Doctor,
        ).map { Action(it) }
        return if (requester has permissions) {
            val newPatient = try {
                Patient(patient, emptySet())
            } catch (e: IllegalArgumentException) {
                return Failure(InvalidArguments(e.message))
            }
            doctorIds
                .map {
                    doctorRepository.getOneOrNull(it.toLong()) ?: return Failure(DoesNotExist())
                }
                .forEach { newPatient.addDoctor(doctor = it) }

            val savedPatient = patientRepository.save(newPatient)
            Success(savedPatient)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun removePatientByPID(pid: Long, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        val deletePatient = Action(Crud.DELETE to Tables.Patient)
        return deletePatient.getAuthorization(requester) {
            val toBeDeleted = patientRepository.getOneOrNull(pid) ?: return@getAuthorization Failure(DoesNotExist(""))
            patientRepository.delete(toBeDeleted)
            Success(toBeDeleted)
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<List<Patient>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Patient) {
            val doctor = try {
                doctorRepository.getOne(did)
            } catch (failure: JpaObjectRetrievalFailureException) {
                if (failure.cause is EntityNotFoundException) {
                    return Failure(DoesNotExist("doctor with did $did was not found"))
                } else {
                    throw failure
                }
            }
            doctorPatientRepository.getAllByDoctorId(doctor.id).map { it.patient }
        }
    }

    override suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Patient) {
            vid ?: return Failure(InvalidArguments("cannot find visit with null vid"))
            try {
                visitRepository.getOne(vid).patient
            } catch (e: JpaObjectRetrievalFailureException) {
                null
            }
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
        val entityManager: EntityManager = sessionFactory.createEntityManager()!!
        val criteriaBuilder: CriteriaBuilder = entityManager.criteriaBuilder!!
        val criteriaQuery: CriteriaQuery<Patient> = criteriaBuilder.createQuery(Patient::class.java)!!
        val itemRoot: Root<Patient> = criteriaQuery.from(Patient::class.java)!!
        val arrayOfPredicates = example.toPredicate(criteriaBuilder, itemRoot).toTypedArray()
        val finalPredicate: Predicate = if (strict) {
            criteriaBuilder.and(*arrayOfPredicates)
        } else {
            criteriaBuilder.or(*arrayOfPredicates)
        }
        criteriaQuery.select(itemRoot).where(finalPredicate)
        return Success(entityManager.createQuery(criteriaQuery).resultList)
    }

    override suspend fun getMany(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField,
        requester: MediqToken
    ): CustomResult<List<Patient>, OrmFailureReason> {
        val size = to - from
        val page = to / size - 1
        return authenticateAndThen(requester, Crud.READ to Tables.Patient) {
            patientRepository.findAll(PageRequest.of(page, size, Sort.by(sortedBy.fieldName))).toList()
        }
    }

    override suspend fun update(
        patientInput: GraphQLPatientInput,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val updatePatients = Crud.UPDATE to Tables.Patient
        return if (requester can updatePatients) {
            val pid = patientInput.pid.getOrNull() ?: return Failure(InvalidArguments())
            val patient = patientRepository.getOneOrNull(pid.toLong()) ?: return Failure(DoesNotExist())
            try {
                patient.setByGqlInput(patientInput)
                Success(patient)
            } catch (e: IllegalArgumentException) {
                Failure(InvalidArguments(e.message))
            }
        } else {
            Failure(NotAuthorized(requester, "not allowed to $updatePatients"))
        }
    }

    private fun searchForName(query: String): List<Patient> {
        return patientRepository.getAllByFirstNameLikeOrMiddleNameLikeOrLastNameLike(query, query, query)
    }

    private fun String.isNotBlankAndOnlyAlpha(): Boolean {
        return this.isNotBlank() && this.all { it.isLetter() }
    }
}
