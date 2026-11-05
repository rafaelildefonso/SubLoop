package com.subloop.data

import android.database.sqlite.SQLiteConstraintException
import com.subloop.network.ExchangeRateApi
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class SubLoopRepository(
    private val userDao: UserDao,
    private val subscriptionDao: SubscriptionDao,
    private val sessionManager: SessionManager,
    private val exchangeRateApi: ExchangeRateApi,
) {
    val currentUserId: Long? get() = sessionManager.currentUserId

    suspend fun register(name: String, email: String, password: String): Result<Long> = runCatching {
        require(name.isNotBlank()) { "Informe seu nome." }
        require(EMAIL_REGEX.matches(email.trim())) { "Informe um e-mail válido." }
        require(password.length >= 6) { "A senha precisa ter pelo menos 6 caracteres." }

        val id = try {
            userDao.insert(
                UserEntity(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    passwordHash = password.sha256(),
                ),
            )
        } catch (_: SQLiteConstraintException) {
            throw IllegalArgumentException("Este e-mail já está cadastrado.")
        }
        sessionManager.save(id)
        id
    }

    suspend fun login(email: String, password: String): Result<Long> = runCatching {
        require(EMAIL_REGEX.matches(email.trim()) && password.length >= 6) {
            "E-mail ou senha inválidos."
        }
        val user = userDao.findByEmail(email.trim().lowercase())
        if (user == null || user.passwordHash != password.sha256()) {
            throw IllegalArgumentException("E-mail ou senha inválidos.")
        }
        sessionManager.save(user.id)
        user.id
    }

    fun logout() = sessionManager.clear()

    fun observeSubscriptions(userId: Long): Flow<List<SubscriptionEntity>> =
        subscriptionDao.observeForUser(userId)

    fun observeSubscription(id: Long, userId: Long): Flow<SubscriptionEntity?> =
        subscriptionDao.observeById(id, userId)

    suspend fun addSubscription(
        userId: Long,
        name: String,
        amountCents: Long,
        recurrence: String,
        nextDueDate: String,
        category: String,
        alertEnabled: Boolean,
    ): Long = subscriptionDao.insert(
        SubscriptionEntity(
            userId = userId,
            name = name.trim(),
            amountCents = amountCents,
            recurrence = recurrence,
            nextDueDate = nextDueDate,
            category = category,
            alertEnabled = alertEnabled,
        ),
    )

    suspend fun update(subscription: SubscriptionEntity) = subscriptionDao.update(subscription)
    suspend fun delete(subscription: SubscriptionEntity) = subscriptionDao.delete(subscription)

    suspend fun loadUsdBrlRate(): Result<Pair<Double, String>> = runCatching {
        val response = exchangeRateApi.latest()
        val rate = requireNotNull(response.rates["BRL"]) { "Cotação indisponível." }
        rate to response.date
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
