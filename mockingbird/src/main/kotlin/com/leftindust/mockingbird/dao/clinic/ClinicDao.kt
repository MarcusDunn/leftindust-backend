package com.leftindust.mockingbird.dao.clinic

@Deprecated("Prefer smaller interfaces")
interface ClinicDao : ReadClinicDao, CreateClinicDao, UpdateClinicDao, DeleteClinicDao {
    override fun necessaryPermissions() = ReadClinicDao.necessaryPermissions +
            CreateClinicDao.necessaryPermissions +
            UpdateClinicDao.necessaryPermissions +
            DeleteClinicDao.necessaryPermissions
}
