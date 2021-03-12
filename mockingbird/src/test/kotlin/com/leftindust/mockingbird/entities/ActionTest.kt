package com.leftindust.mockingbird.entities

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import org.junit.jupiter.api.Test

class ActionTest {

    @Test
    internal fun `test isSuperset with same actions`() {
        val action1 = Action(Crud.READ to Tables.Patient)
        val action2 = Action(Crud.READ to Tables.Patient)
        assert(action1 isSuperset action2)
        assert(action2 isSuperset action1)
    }

    @Test
    internal fun `test isSuperset with strict superset`() {
        val action1 = Action(Crud.CREATE to Tables.Patient)

        val action2 = Action(
            referencedTableName = Tables.Patient,
            permissionType = Crud.CREATE,
            rowId = 10,
        )

        assert(action1 isSuperset action2) { "assert failed on ($action1 isSuperset $action2)" }
    }

    @Test
    internal fun `test isSuperset with strict subset`() {
        val action1 = Action(
            referencedTableName = Tables.Group,
            permissionType = Crud.CREATE,
            rowId = 10,
        )

        val action2 = Action(
            referencedTableName = Tables.Group,
            permissionType = Crud.CREATE,
        )


        assert(!(action1 isSuperset action2))
    }

    @Test
    internal fun `test isSuperset with friendlyName`() {
        val action1 = Action(
            referencedTableName = Tables.Group,
            permissionType = Crud.CREATE,
            rowId = 10,
        )


        val action2 = Action(
            referencedTableName = Tables.Group,
            permissionType = Crud.CREATE,
            rowId = 10,
        )

        assert(action1 isSuperset action2)

    }
}