package api.models

import kotlinx.serialization.Serializable
import lol.unsession.db.models.Teacher

@Serializable
data class Review (
    val id: Int?,
    val user: User,
    val teacher: Teacher,

    val globalRating: Int,
    val labsRating: Int?,
    val hwRating: Int?,
    val examRating: Int?,

    val kindness: Int?,
    val responsibility: Int?,
    val individuality: Int?,
    val humour: Int?,

    val createdTimestamp: Long,
    val comment: String?,
)
