package utils.localization

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.ResourceStringDesc

actual object Localized {
    @Composable
    actual fun ResourceStringDesc.localString(): String {
        return this.toString(context = LocalContext.current)
    }
    @Composable
    actual fun PluralStringDesc.localPlural(): String {
        return this.toString()
    }
    actual object Formatted {
        @Composable
        actual fun StringResource.localString(vararg args: Any): String {
            return this.toString()
        }
        @Composable
        actual fun PluralStringDesc.localPlural(vararg args: Any): String {
            return this.toString()
        }
    }
}