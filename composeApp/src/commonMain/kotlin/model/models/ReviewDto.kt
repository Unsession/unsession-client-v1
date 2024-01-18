package lol.unsession.db.models

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto (
    val id: Int?,
    val userId: Int,
    val teacherId: Int,

    val globalRating: Int,
    val labsRating: Int?,
    val hwRating: Int?,
    val examRating: Int?,

    val kindness: Int?,
    val responsibility: Int?,
    val individuality: Int?,
    val humour: Int?,

    val createdTimestamp: Int,
    val comment: String?,
)
