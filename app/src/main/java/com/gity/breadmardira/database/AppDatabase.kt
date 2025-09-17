package com.gity.breadmardira.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gity.breadmardira.dao.UserDao
import com.gity.breadmardira.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bread_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database with initial data Admin
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).userDao()
                                    .insert(
                                        User(
                                            username = "admin",
                                            password = "admin",
                                            role = "admin"
                                        )
                                    )
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }
    }
}


