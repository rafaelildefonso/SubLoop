package com.subloop.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId")],
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val amountCents: Long,
    val recurrence: String,
    val nextDueDate: String,
    val category: String,
    val alertEnabled: Boolean,
    val alertDaysBefore: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
)
