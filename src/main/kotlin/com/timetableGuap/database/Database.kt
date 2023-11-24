package com.timetableGuap.database

import com.timetableGuap.database.data.Building
import com.timetableGuap.database.data.DatabaseItem
import com.timetableGuap.database.data.Room
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory(private val nameDatabase: String = "Kursach") {
    private val userDatabase = "postgres"
    private val passwordDatabase = "2234"

    private val database: Database by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/$nameDatabase",
            driver = "org.postgresql.Driver",
            user = userDatabase,
            password = passwordDatabase
        )
    }

    fun init() {
        addItemsInDatabase(
            listOf(
                Building(2, "Гастелло", "Г"),
                Building(3, "ФСПО ГУАП", "К")
            )
        )

        addItemInDatabase(Room(3, "508"))
    }

    fun addItemInDatabase(item: DatabaseItem, isUpdatePolicy: Boolean = true) {
        transaction(database) {
            val query =
                if (isUpdatePolicy && item.needUpdate())
                    "INSERT INTO ${item.getDatabaseTableNameWithPostfix()} " +
                            "ON CONFLICT (${item.getIdName()}) DO update set ${item.getDatabaseUpdatePostfix()};"
                else "INSERT INTO ${item.getDatabaseTableNameWithPostfix()} " +
                        "ON CONFLICT (${item.getIdName()}) DO NOTHING;"
            val statement = connection.prepareStatement(query, false)
            statement.fillParameters(item.getColumnItems())

            statement.executeUpdate()
        }
    }

    fun addItemsInDatabase(items: List<DatabaseItem>, isUpdatePolicy: Boolean = true) {
        transaction(database) {
            items.forEach {
                val query =
                    if (isUpdatePolicy && it.needUpdate())
                    "INSERT INTO ${it.getDatabaseTableNameWithPostfix()} " +
                            "ON CONFLICT (${it.getIdName()}) DO update set ${it.getDatabaseUpdatePostfix()};"
                else "INSERT INTO ${it.getDatabaseTableNameWithPostfix()} " +
                            "ON CONFLICT (${it.getIdName()}) DO NOTHING;"
                println(query)
                val statement = connection.prepareStatement(query, false)
                statement.fillParameters(it.getColumnItems())
                statement.executeUpdate()
            }
        }
    }
}