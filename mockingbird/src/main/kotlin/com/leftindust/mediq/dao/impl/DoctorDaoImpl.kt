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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
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
    override suspend fun getByPatient(pid: Int, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason> {

        return if (requester can (Crud.READ to Tables.Patient)) {
            val patient = patientRepository.getPatientByPid(pid) ?: return Failure(DoesNotExist())
            val doctors = doctorPatientRepository.getAllByPatientId(patient.id!!).map { it.doctor }
            Success(doctors)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Doctor, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Visit)) {
            vid ?: return Failure(InvalidArguments("cannot get by null vid"))
            val visit = try {
                visitRepository.getOne(vid)
            } catch (e: JpaObjectRetrievalFailureException) {
                return Failure(DoesNotExist())
            }
            Success(visit.doctor)
        } else {
            Failure(NotAuthorized(requester,"cannot READ to Visit"))
        }
    }

    override suspend fun getByDoctor(did: Int, requester: MediqToken): CustomResult<Doctor, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Doctor) { doctorRepository.getById(did) }
    }
}