package com.timetableGuap.database

import com.timetableGuap.database.data.*
import com.timetableGuap.network.response.EventResponse
import com.timetableGuap.network.response.LessonResponse
import com.timetableGuap.network.response.Links
import com.timetableGuap.network.response.PaginationData
import com.timetableGuap.time.DataTime
import com.timetableGuap.util.Resource
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

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
            println(query)

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

    fun <T> getAllItemsFromTable(nameDatabase: String, convertToObject: (ResultSet) -> T): MutableList<T> {
        return transaction(database) {
            val query = "SELECT * FROM $nameDatabase;"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            val listObjects = mutableListOf<T>()
            while (result.next()) {
                listObjects.add(convertToObject(result))
            }
            listObjects
        }
    }

    fun getCountItemsInTable(nameDatabase: String, condition: String = ""): Int {
        return transaction(database) {
            val query = "SELECT COUNT(*) FROM $nameDatabase $condition;"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            result.next()
            result.getInt(1)
        }
    }

    fun getMaxIdInDatabase(nameDatabase: String, nameId: String): Int {
        return transaction(database) {
            val query = "SELECT MAX($nameId) FROM $nameDatabase"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            result.next()
            result.getInt(1)
        }
    }

    fun getLessonId(number: Int, filter: String, subjectId: Int, dateTimeStart: String): Int {
        return transaction(database) {
            val query = "select lesson.id\n" +
                    "from lesson\n" +
                    "where number = $number\n" +
                    "  and filtertimetable = '$filter'\n" +
                    "  and subjectid = $subjectId" +
                    "  and datetimestart = '$dateTimeStart';"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            if (result.next())
                result.getInt(1)
            else
                getCountItemsInTable(LessonDatabase.nameTable)
        }
    }

    fun getUserId(login: String): Int {
        return transaction(database) {
            val query = "select userdata.userid\n" +
                    "from userdata\n" +
                    "where login = '$login';"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            if (result.next())
                -1
            else
                getCountItemsInTable(UserData.name)
        }
    }

    private fun getEventId(nameEvent: String): Int {
        return transaction(database) {
            val query = """select "Event".id
                    from "Event"
                    where name = '$nameEvent';"""
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            if (result.next())
                -1
            else
                getCountItemsInTable(Event.nameTable)
        }
    }

    private fun getTypeId(nameType: String, ownerId: Int): Int {
        return transaction(database) {
            val query = """select "Type".id
                    from "Type"
                    where name = '$nameType' AND ownerid = $ownerId;"""
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            if (result.next())
                -1
            else
                getMaxIdInDatabase(Type.nameTable, Type.nameId) + 1
        }
    }

    fun getLessons(filter: String, dateTimeStart: DataTime, dateTimeEnd: DataTime): MutableList<LessonResponse> {
        return transaction(database) {
            val query = "select lesson.*,\n" +
                    "       array_agg(DISTINCT t.id)                              as teacherIds,\n" +
                    "       array_agg(DISTINCT G.id)                              as groupsIds,\n" +
                    "       array_agg(DISTINCT concat(l.buildingid, ':', l.name)) as roomIds,\n" +
                    "       s.name,\n" +
                    "       s.duration,\n" +
                    "       s.typeid\n" +
                    "from lesson\n" +
                    "         inner join public.subject s on s.id = lesson.subjectid\n" +
                    "         inner join public.lessongroup lg on lg.lessonid = lesson.id\n" +
                    "         inner join public.\"Group\" G on G.id = lg.groupid\n" +
                    "         inner join public.lessonroom l on lesson.id = l.lessonid\n" +
                    "         inner join public.room r on r.buildingid = l.buildingid and r.name = l.name\n" +
                    "         inner join public.lessonteacher l2 on lesson.id = l2.lessonid\n" +
                    "         inner join public.teacher t on t.id = l2.teacherid\n" +
                    "where lesson.filtertimetable = '$filter' AND lesson.datetimestart between '${dateTimeStart.getIsoFormat()}' and '${dateTimeEnd.getIsoFormat()}'\n" +
                    "group by lesson.id, s.id;"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            val listObjects = mutableListOf<LessonResponse>()
            while (result.next()) {
                listObjects.add(LessonResponse.convertToLessonResponse(result))
            }
            listObjects
        }
    }

    fun createUser(login: String, password: String, firstName: String, secondName: String, filter: String): Resource<User> {
        val userId = getUserId(login)
        if (userId == -1)
            return Resource.Error("This user already exists")
        else
            addItemsInDatabase(listOf(User(userId, firstName, secondName, filter), UserData(login, password, userId)))

        return authUser(login, password)
    }

    private fun getUser(userId: Int): User {
        return transaction(database) {
            val query = "select *\n" +
                    "from public.\"User\"\n" +
                    "where id = $userId;"
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            result.next()
            User.convertToUser(result)
        }
    }

    fun authUser(login: String, password: String): Resource<User> {
        return transaction(database) {
            val query = "select userdata.userid\n" +
                    "from userdata\n" +
                    "where login = '$login' and hexpassword = '${generateHash(password, User::class.java.name)}';"
            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            if (result.next())
                Resource.Success(getUser(result.getInt(1)))
            else
                Resource.Error("Data is not valid")
        }
    }

    fun createEvent(event: Event, typeIds: List<Int>): Resource<String> {
        val eventId = getEventId(event.name)
        if (eventId == -1)
            return Resource.Error("Event with this name already exists")
        else
            addItemsInDatabase(listOf(event.copy(id = eventId)) + typeIds.map { EventType(eventId, it) })

        return Resource.Success("Event created", 201)
    }

    fun createType(eventType: Type): Resource<String> {
        val typeId = getTypeId(eventType.name, eventType.ownerId)
        if (typeId == -1)
            return Resource.Error("Type with this name already exists", 409)
        else
            addItemsInDatabase(listOf(eventType.copy(id = typeId), TypeUser(eventType.ownerId, typeId)))

        return Resource.Success("Type created", 201)
    }

    fun getEvents(userId: Int, dateTimeStart: DataTime, dateTimeEnd: DataTime): List<EventResponse> {
        return transaction(database) {
            val query = """SELECT "Event".*, array_agg(DISTINCT T.typeid)   as typeIds  FROM "Event"
inner join public.eventtype T on T.eventid = "Event".id
inner join public."Type" T2 on T2.id = T.typeid
where T2.ownerid = $userId AND datetimestart between '${dateTimeStart.getIsoFormat()}' and '${dateTimeEnd.getIsoFormat()}'
group by "Event".id;"""

            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            val listObjects = mutableListOf<EventResponse>()
            while (result.next()) {
                listObjects.add(EventResponse.convertToEventResponse(result))
            }
            listObjects
        }
    }

    fun getPaginationData(
        nameTable: String,
        convertToDatabaseItem: (ResultSet) -> DatabaseItem,
        pageSize: Int,
        page: Int,
        condition: String = ""
    ): PaginationData {
        return transaction(database) {
            val countItemsInTable = getCountItemsInTable(nameTable)

            val query = """
                SELECT * FROM $nameTable $condition LIMIT $pageSize OFFSET ${(page - 1) * pageSize};
            """.trimIndent()

            println(query)
            val statement = connection.prepareStatement(query, false)
            val result = statement.executeQuery()
            val listObjects = mutableListOf<DatabaseItem>()
            while (result.next()) {
                listObjects.add(convertToDatabaseItem(result))
            }
            PaginationData(
                Links(page > 1 && pageSize * (page-2) < countItemsInTable, pageSize * page < countItemsInTable),
                countItemsInTable,
                listObjects
            )
        }
    }
}

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

@OptIn(ExperimentalStdlibApi::class)
fun generateHash(password: String, salt: String): String {
    val combinedSalt = "$salt$SECRET".toByteArray()
    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded
    return hash.toHexString()
}