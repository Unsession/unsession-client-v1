package api.models

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Int? = null,
    val userId: Int,
    val teacherId: Int,

    val globalRating: Int,
    val difficultyRating: Int? = null,
    val boredomRating: Int? = null,
    val toxicityRating: Int? = null,
    val educationalValueRating: Int? = null,
    val personalQualitiesRating: Int? = null,

    val createdTimestamp: Int,
    val comment: String? = null,
)
