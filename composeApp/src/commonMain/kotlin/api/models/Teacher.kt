package api.models

import kotlinx.serialization.Serializable

@Serializable
open class Teacher (
    val id: Int,
    val name: String,
    val email: String?,
    val department: String,
    val rating: Double?,
)
