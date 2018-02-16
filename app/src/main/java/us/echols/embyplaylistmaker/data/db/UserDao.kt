package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.*
import io.reactivex.Single
import us.echols.embyplaylistmaker.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): Single<MutableList<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUsers(users: List<User>)

    @Query("SELECT * FROM users WHERE last_active = 1")
    fun getActiveUser(): Single<User>

    @Update
    fun setActiveUser(user: User)

    @Query("UPDATE users SET last_active = 0")
    fun setAllUserInactive()

    @Query("SELECT password FROM users WHERE id = :id")
    fun getUserPassword(id: String): Single<String>

    @Query("UPDATE users SET password = :password WHERE id = :id")
    fun updateUserPassword(id: String, password: String)

    @Query("SELECT token FROM users WHERE id = :id")
    fun getAccessToken(id: String): Single<String>

    @Query("UPDATE users SET token = :token WHERE id = :id")
    fun updateAccessToken(id: String, token: String)

}