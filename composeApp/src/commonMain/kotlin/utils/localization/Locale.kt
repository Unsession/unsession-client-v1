package utils.localization

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.PluralFormattedStringDesc
import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.ResourceStringDesc
import dev.icerock.moko.resources.desc.StringDesc

object Res {
    fun getString(key: StringResource): ResourceStringDesc {
        return StringDesc.Resource(key)
    }

    fun getPlural(key: PluralsResource, quantity: Int): PluralStringDesc {
        return StringDesc.Plural(key, quantity)
    }

    object Formatted {
        fun getString(key: StringResource, vararg args: Any): ResourceFormattedStringDesc {
            return StringDesc.ResourceFormatted(key, args)
        }

        fun getPlural(key: PluralsResource, quantity: Int, vararg args: Any): PluralFormattedStringDesc {
            return StringDesc.PluralFormatted(key, quantity, args)
        }
    }
}

expect object Localized {
    @Composable
    fun ResourceStringDesc.localString(): String
    @Composable
    fun PluralStringDesc.localPlural(): String
    object Formatted {
        @Composable
        fun StringResource.localString(vararg args: Any): String
        @Composable
        fun PluralStringDesc.localPlural(vararg args: Any): String
    }
}