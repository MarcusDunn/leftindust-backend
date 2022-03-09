package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLDoctorExample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class DoctorDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val doctorPatientRepository: HibernateDoctorPatientRepository,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val eventRepository: HibernateEventRepository,
    @Autowired private val clinicRepository: HibernateClinicRepository,
    @Autowired private val entityManager: EntityManager,
) : DoctorDao, AbstractHibernateDao(authorizer) {
    override suspend fun getPatientDoctors(pid: GraphQLPatient.ID, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) withContext(Dispatchers.IO) {
            val patient = patientRepository.getById(pid.id)
            doctorPatientRepository.getAllByPatientId(patient.id!!).map { it.doctor }
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) withContext(Dispatchers.IO) {
            eventRepository.getById(eid.id).doctors.also { Hibernate.initialize(it) }
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Doctor {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) withContext(Dispatchers.IO) {
            doctorRepository.getById(did.id)
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun addDoctor(
        doctor: GraphQLDoctorInput,
        requester: MediqToken,
        user: MediqUser?
    ): Doctor {
        val createDoctor = Crud.CREATE to Tables.Doctor
        return if (requester can createDoctor) {
            val patients = doctor.patients?.let { patientRepository.getByIds(it.map { pid -> pid.id }) } ?: emptySet()
            val doctorEntity = Doctor(doctor, user, patients)
            withContext(Dispatchers.IO) { doctorRepository.save(doctorEntity) }
        } else {
            throw NotAuthorizedException(requester, createDoctor)
        }
    }

    override suspend fun editDoctor(doctor: GraphQLDoctorEditInput, requester: MediqToken): Doctor {
        val updateDoctor = Crud.UPDATE to Tables.Doctor
        if (requester can updateDoctor) {
            val doctorEntity = withContext(Dispatchers.IO) { doctorRepository.getById(doctor.did.id) }
            doctorEntity.setByGqlInput(doctor, entityManager)
            return doctorEntity
        } else {
            throw NotAuthorizedException(requester, updateDoctor)
        }
    }

    override suspend fun getByClinic(clinic: GraphQLClinic.ID, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) withContext(Dispatchers.IO) {
            clinicRepository.getById(clinic.id).doctors.also { Hibernate.initialize(it) }
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getByUser(uid: String, requester: MediqToken): Doctor? {
        val readDoctors = Crud.READ to Tables.Doctor
        val readUsers = Crud.READ to Tables.User
        return if (requester can listOf(readDoctors, readUsers)) withContext(Dispatchers.IO) {
            doctorRepository.findByUser_UniqueId(uid)
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getMany(range: GraphQLRangeInput, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) withContext(Dispatchers.IO) {
            doctorRepository.findAll(range.toPageable()).content
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun searchByExample(example: GraphQLDoctorExample, requester: MediqToken): Collection<Doctor> {
        if (requester can (Crud.READ to Tables.Doctor)) {

            val criteriaBuilder = entityManager.criteriaBuilder
            val criteriaQuery = criteriaBuilder.createQuery(Doctor::class.java)
            val root = criteriaQuery.from(Doctor::class.java)

            val predicate = example.toPredicate(criteriaBuilder, root)

            criteriaQuery.where(predicate)

            val doctorQuery =  entityManager.createQuery(criteriaQuery.where(predicate))

            return withContext(Dispatchers.IO) { doctorQuery.resultList }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Doctor)
        }
    }
}