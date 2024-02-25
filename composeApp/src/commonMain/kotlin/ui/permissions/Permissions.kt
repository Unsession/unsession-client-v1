package ui.permissions

import androidx.compose.runtime.Composable
import api.models.Access
import api.models.User

fun Collection<Any>.containsAny(list: List<Access>): Boolean {
    return this.any { list.contains(it) }
}

@Composable
fun WithPermission(
    permission: Access,
    fail: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (User.get().permissions.contains(permission)) {
        content()
    } else {
        fail()
    }
}

@Composable
fun WithAnyPermission(
    vararg permissions: Access,
    fail: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (User.get().permissions.containsAny(permissions.toList())) {
        content()
    } else {
        fail()
    }
}

@Composable
fun WithAllPermissions(
    vararg permissions: Access,
    fail: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (User.get().hasAccess(permissions.toList())) {
        content()
    } else {
        fail()
    }
}
