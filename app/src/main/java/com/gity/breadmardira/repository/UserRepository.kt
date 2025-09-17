package com.gity.breadmardira.repository

import com.gity.breadmardira.dao.UserDao
import com.gity.breadmardira.model.User

class UserRepository(private val dao: UserDao) {
    suspend fun register(user: User) = dao.insert(user)
    suspend fun login(username: String, password: String) = dao.login(username, password)
    suspend fun getUser(username: String) = dao.getUser(username)
}