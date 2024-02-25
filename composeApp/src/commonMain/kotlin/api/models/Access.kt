package api.models

/**
 * ПОРЯДОК ИМЕЕТ ЗНАЧЕНИЕ
 * */
enum class Access {
    BigRedButton, // Big Red Button

    Homeworks, // homeworks
    HomeworksAdding, // homeworks adding

    Tests, // tests
    TestsAdding, // tests adding
    TestsRating, // tests rating
    TestAnswers, // tests answers

    Teachers, // teachers
    TeachersAdding, // teachers adding
    TeachersReviewing, // teachers reviewing

    Users, // users
    UsersAdding, // users adding
    UsersRemoving, // users removing
    UsersBlocking, // users blocking
    UsersRolesChanging, // users roles changing
    UsersProfileInfoEditing, // users profile info editing

    SS, // superuser
}

val adminAccess = arrayOf(Access.SS, Access.BigRedButton, Access.Users, Access.UsersAdding, Access.UsersBlocking, Access.UsersRemoving, Access.UsersRolesChanging)
val usersAccess = arrayOf(Access.Users, Access.UsersAdding, Access.UsersRemoving, Access.UsersBlocking, Access.UsersRolesChanging, Access.UsersProfileInfoEditing)