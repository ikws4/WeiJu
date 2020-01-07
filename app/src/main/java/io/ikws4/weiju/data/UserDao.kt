package io.ikws4.weiju.data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserFormLiveData(): LiveData<User?>

    @Query("SELECT * FROM user")
    fun getUser(): User

    @Query("SELECT freeSogouApiAmount FROM user")
    fun getFreeSogouApiAmount(): Cursor

    @Query("UPDATE user SET freeSogouApiAmount =:amount")
    fun setFreeSogouApiAmount(amount: Int)

    @Query("UPDATE user SET freeSogouApiAmount = freeSogouApiAmount + :amount")
    fun increaseFreeSogouApiAmount(amount: Int)

    @Query("UPDATE user SET freeSogouApiAmount = freeSogouApiAmount - :amount")
    fun decreaseFreeSogouApiAmount(amount: Int)

    @Insert
    fun insert(user: User)
}