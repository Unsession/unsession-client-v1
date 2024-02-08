package api.models

import kotlinx.serialization.Serializable

@Serializable
data class Review (
    val id: Int?,
    val user: User,
    val teacher: Teacher,

    val globalRating: Int,
    val difficultyRating: Int?,
    val boredomRating: Int?,
    val toxicityRating: Int?,
    val educationalValueRating: Int?,
    val personalQualitiesRating: Int?,

    val createdTimestamp: Long,
    val comment: String?,
)
