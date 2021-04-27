package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
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

    override suspend fun getByPID(pID: Long, requester: MediqToken): Patient {
        if (requester can (Crud.READ to Tables.Patient)) {
            return patientRepository.getOne(pID)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        requester: MediqToken
    ): Patient {
        if (requester can listOf(Crud.CREATE to Tables.Patient, Crud.UPDATE to Tables.Doctor)) {
            val newPatient = Patient(patient, sessionFactory.currentSession)
            return patientRepository.save(newPatient)
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.Patient, Crud.UPDATE to Tables.Doctor)
        }
    }

    override suspend fun removeByPID(pid: Long, requester: MediqToken): Patient {
        return if (requester can (Crud.DELETE to Tables.Patient)) {
            val toBeDeleted = patientRepository.getOne(pid)
            patientRepository.delete(toBeDeleted)
            toBeDeleted
        } else {
            throw NotAuthorizedException(requester, Crud.DELETE to Tables.Patient)
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
    ): Patient {
        val permissions = listOf(Crud.UPDATE to Tables.Doctor, Crud.UPDATE to Tables.Doctor)
        return if (requester can permissions) {
            val patient = patientRepository.getOne(patientInput.toLong())
            val doctor = doctorRepository.getOne(doctorInput.toLong())
            patient.addDoctor(doctor)
        } else {
            throw NotAuthorizedException(requester, *permissions.toTypedArray())
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
        patientInput: GraphQLPatientEditInput,
        requester: MediqToken
    ): Patient {
        return if (requester can (Crud.UPDATE to Tables.Patient)) {
            val patient = patientRepository.getOne(patientInput.pid.toLong())
            patient.apply {
                setByGqlInput(patientInput, sessionFactory.currentSession)
            }
        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Patient)
        }
    }

    override suspend fun getPatientsByPids(pids: List<ID>, requester: MediqToken): Collection<Patient> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            patientRepository.findAllById(pids.map { it.toLong() })
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun searchByExample(example: GraphQLPatientExample, requester: MediqToken): Collection<Patient> {
        if (requester can (Crud.READ to Tables.Patient)) {
            val session = sessionFactory.currentSession

            val criteriaBuilder = session.criteriaBuilder
            val criteriaQuery = criteriaBuilder.createQuery(Patient::class.java)
            val root = criteriaQuery.from(Patient::class.java)

            val predicate = example.toPredicate(criteriaBuilder, root)

            criteriaQuery.where(predicate)

            return session.createQuery(criteriaQuery.where(predicate)).resultList
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }
}
