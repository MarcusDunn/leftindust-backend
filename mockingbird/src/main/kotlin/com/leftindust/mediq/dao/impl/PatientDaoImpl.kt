package com.leftindust.mediq.dao.impl

import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.impl.repository.*
import com.leftindust.mediq.extensions.*
import com.leftindust.mediq.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mediq.graphql.types.input.GraphQLPatientInput
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
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

    override suspend fun getByPID(pID: Int, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        val readToDatabase = Action(Crud.READ to Tables.Patient)
        return readToDatabase.getAuthorization(requester) {
            Success(
                patientRepository.getPatientByPid(pID)
                    ?: return@getAuthorization Failure(DoesNotExist())
            )
        }
    }

    override suspend fun getManyGroupedBySorted(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField,
        requester: MediqToken,
    ): CustomResult<Map<String, List<Patient>>, OrmFailureReason> {
        val readToPatient = Action(Crud.READ to Tables.Patient)

        return readToPatient.getAuthorization(requester) {
            val size = to - from
            val page = to / size - 1
            Success(
                patientRepository
                    .findAll(PageRequest.of(page, size, Sort.by(sortedBy.fieldName)))
                    .groupBy { patient ->
                        sortedBy
                            .instanceValue(patient)
                            .toString()
                            .first()
                            .toString()
                    })
        }
    }

    override suspend fun addNewPatient(
        patient: Patient,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason> {
        val writeToPatient = Action(Crud.CREATE to Tables.Patient)
        return writeToPatient.getAuthorization(requester) {
            if (patientRepository.getPatientByPid(patient.pid) == null) {
                Success(patientRepository.save(patient))
            } else {
                Failure(AlreadyExists("collision with pid ${patient.pid}"))
            }
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
                    doctorRepository.getById(it.toLong()) ?: return Failure(DoesNotExist())
                }
                .forEach { newPatient.addDoctor(doctor = it) }

            val savedPatient = patientRepository.save(newPatient)
            Success(savedPatient)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun removePatientByPID(pID: Int, requester: MediqToken): CustomResult<Patient, OrmFailureReason> {
        val deletePatient = Action(Crud.DELETE to Tables.Patient)
        return deletePatient.getAuthorization(requester) {
            val toBeDeleted = patientRepository.getPatientByPid(pID) ?: return@getAuthorization Failure(DoesNotExist())
            patientRepository.delete(toBeDeleted)
            Success(toBeDeleted)
        }
    }

    override suspend fun searchByName(
        query: String,
        requester: MediqToken
    ): CustomResult<List<Patient>, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            if (query.isNotBlankAndOnlyAlpha()) {
                Success(searchForName(query))
            } else {
                Failure(InvalidArguments("query sting must not be empty all chars must match [a-zA-Z]"))
            }
        } else {
            Failure(NotAuthorized(requester, "cannot ${Crud.READ to Tables.Patient}"))
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<List<Patient>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Patient) {
            val doctor = doctorRepository.getOne(did) ?: return@authenticateAndThen null
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
            val patient = patientRepository.getPatientByPid(patientInput.toInt())
                ?: return Failure(DoesNotExist("patient does not exist"))
            val doctor = doctorRepository.getById(doctorInput.toLong())
                ?: return Failure(DoesNotExist("doctor does not exist"))
            return Success(patient.addDoctor(doctor))
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun searchByExample(
        example: GraphQLPatientExample,
        requester: MediqToken
    ): CustomResult<List<Patient>, OrmFailureReason> {
        val entityManager: EntityManager = sessionFactory.createEntityManager()!!
        val criteriaBuilder: CriteriaBuilder = entityManager.criteriaBuilder!!
        val criteriaQuery: CriteriaQuery<Patient> = criteriaBuilder.createQuery(Patient::class.java)!!
        val itemRoot: Root<Patient> = criteriaQuery.from(Patient::class.java)!!

        val finalPredicate: Predicate =
            criteriaBuilder.and(*example.toPredicate(criteriaBuilder, itemRoot).toTypedArray())
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
            val pid = patientInput.pid.getOrNull()?.toInt() ?: return Failure(InvalidArguments())
            val patient = patientRepository.getPatientByPid(pid) ?: return Failure(DoesNotExist())
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
