package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.*
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.hibernate.Hibernate
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Transactional
@Repository
class PatientDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val doctorPatientRepository: HibernateDoctorPatientRepository,
    @Autowired private val eventRepository: HibernateEventRepository,
    @Autowired private val visitRepository: HibernateVisitRepository,
    @Autowired private val sessionFactory: SessionFactory,
) : PatientDao, AbstractHibernateDao(authorizer) {

    override suspend fun getByPID(pid: GraphQLPatient.ID, requester: MediqToken): Patient {
        if (requester can (Crud.READ to Tables.Patient)) {
            return patientRepository.getById(pid.id)
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

    override suspend fun removeByPID(pid: GraphQLPatient.ID, requester: MediqToken): Patient {
        return if (requester can (Crud.DELETE to Tables.Patient)) {
            val toBeDeleted = patientRepository.getById(pid.id)
            patientRepository.delete(toBeDeleted)
            toBeDeleted
        } else {
            throw NotAuthorizedException(requester, Crud.DELETE to Tables.Patient)
        }
    }

    override suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Patient> {
        if (requester can (Crud.READ to Tables.Patient)) {
            val doctor = doctorRepository.getById(did.id)
            return doctorPatientRepository.getAllByDoctorId(doctor.id!!).map { it.patient }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun getByVisit(vid: GraphQLVisit.ID, requester: MediqToken): Collection<Patient> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            visitRepository.getById(vid.id).event.patients
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun addDoctorToPatient(
        pid: GraphQLPatient.ID,
        did: GraphQLDoctor.ID,
        requester: MediqToken
    ): Patient {
        val permissions = listOf(Crud.UPDATE to Tables.Doctor, Crud.UPDATE to Tables.Doctor)
        return if (requester can permissions) {
            val patient = patientRepository.getById(pid.id)
            val doctor = doctorRepository.getById(did.id)
            patient.addDoctor(doctor)
        } else {
            throw NotAuthorizedException(requester, *permissions.toTypedArray())
        }
    }


    override suspend fun getMany(
        range: GraphQLRangeInput,
        sortedBy: Patient.SortableField,
        requester: MediqToken
    ): Collection<Patient> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            patientRepository.findAll(range.toPageable(Sort.by(sortedBy.fieldName))).content
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun update(
        patientInput: GraphQLPatientEditInput,
        requester: MediqToken
    ): Patient {
        return if (requester can (Crud.UPDATE to Tables.Patient)) {
            val patient = patientRepository.getById(patientInput.pid.id)
            patient.apply {
                setByGqlInput(patientInput, sessionFactory.currentSession)
            }
        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Patient)
        }
    }

    override suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Collection<Patient> {
        val readEventsAndReadPatient = listOf(Crud.READ to Tables.Event, Crud.READ to Tables.Patient)
        return if (requester can readEventsAndReadPatient) {
            eventRepository.getById(eid.id).patients.also { Hibernate.initialize(it) }
        } else {
            throw NotAuthorizedException(requester, *readEventsAndReadPatient.toTypedArray())
        }
    }

    override suspend fun getPatientsByPids(pids: List<GraphQLPatient.ID>, requester: MediqToken): Collection<Patient> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            patientRepository.findAllById(pids.map { it.id })
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

    override suspend fun getByUser(uid: String, requester: MediqToken): Patient? {
        if (requester can (Crud.READ to Tables.Patient)) {

            return patientRepository.findByUser_UniqueId(uid)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }
}
