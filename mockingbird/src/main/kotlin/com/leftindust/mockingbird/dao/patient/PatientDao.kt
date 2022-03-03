package com.leftindust.mockingbird.dao.patient

@Deprecated("Prefer to use smaller interfaces")
interface PatientDao : ReadPatientDao, DeletePatientDao, UpdatePatientDao, CreatePatientDao {
    override fun necessaryPermissions() = ReadPatientDao.necessaryPermissions +
            CreatePatientDao.necessaryPermissions +
            DeletePatientDao.necessaryPermissions +
            UpdatePatientDao.necessaryPermissions
}