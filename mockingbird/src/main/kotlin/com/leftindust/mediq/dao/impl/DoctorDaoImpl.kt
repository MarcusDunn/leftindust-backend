package com.leftindust.mediq.dao.impl

import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mediq.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mediq.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mediq.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import com.leftindust.mediq.extensions.getOneOrNull
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
    @Autowired private val visitRepository: HibernateVisitRepository,
) : DoctorDao, AbstractHibernateDao(authorizer) {
    override suspend fun getByPatient(pid: Long, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason> {

        return if (requester can (Crud.READ to Tables.Patient)) {
            val patient = patientRepository.getOneOrNull(pid)
                ?: return Failure(DoesNotExist("patient with pid: $pid was not found"))
            val doctors = doctorPatientRepository.getAllByPatientId(patient.id!!).map { it.doctor }
            Success(doctors)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Doctor, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Visit)) {
            vid
                ?: return Failure(InvalidArguments("cannot get by null vid"))
            val visit = visitRepository.getOneOrNull(vid)
                ?: return Failure(DoesNotExist("cannot find visit with vid: $vid"))
            Success(visit.doctor)
        } else {
            Failure(NotAuthorized(requester, "cannot READ to Visit"))
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<Doctor, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Doctor) {
            doctorRepository.getOneOrNull(did)
                ?: return Failure(DoesNotExist("doctor with did: $did was not found"))
        }
    }
}