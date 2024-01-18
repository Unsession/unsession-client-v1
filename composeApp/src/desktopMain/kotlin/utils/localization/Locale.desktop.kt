package utils.localization

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.ResourceStringDesc

actual object Localized {
    @Composable
    actual fun ResourceStringDesc.localString(): String {
        return localized()
    }
    @Composable
    actual fun PluralStringDesc.localPlural(): String {
        return localized()
    }
    actual object Formatted {
        @Composable
        actual fun StringResource.localString(vararg args: Any): String {
            return localized()
        }
        @Composable
        actual fun PluralStringDesc.localPlural(vararg args: Any): String {
            return localized()
        }
    }
}