package api.models

import kotlinx.serialization.Serializable

@Serializable
data class Review (
    val id: Int? = null,
    val user: User? = null,
    val teacher: TeacherDto,

    val globalRating: Int,
    val difficultyRating: Int? = null,
    val boredomRating: Int? = null,
    val toxicityRating: Int? = null,
    val educationalValueRating: Int? = null,
    val personalQualitiesRating: Int? = null,

    val createdTimestamp: Int? = null,
    val comment: String? = null,
)
