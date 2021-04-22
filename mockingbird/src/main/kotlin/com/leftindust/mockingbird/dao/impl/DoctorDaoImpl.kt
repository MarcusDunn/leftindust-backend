package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class DoctorDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val doctorPatientRepository: HibernateDoctorPatientRepository,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val eventRepository: HibernateEventRepository
) : DoctorDao, AbstractHibernateDao(authorizer) {
    override suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) {
            val patient = patientRepository.getOne(pid)
            doctorPatientRepository.getAllByPatientId(patient.id!!).map { it.doctor }
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getByEvent(eid: Long, requester: MediqToken): Collection<Doctor> {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) {
            eventRepository.getOne(eid).doctors
        } else {
            throw NotAuthorizedException(requester, readDoctors)
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): Doctor {
        val readDoctors = Crud.READ to Tables.Doctor
        return if (requester can readDoctors) {
            doctorRepository.getOne(did)
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
            val patients = doctor.patients?.let { patientRepository.getByIds(it) } ?: emptySet()
            val doctorEntity = Doctor(doctor, user, patients)
            doctorRepository.save(doctorEntity)
        } else {
            throw NotAuthorizedException(requester, createDoctor)
        }
    }
}