package utils.localization

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

object Res {
    fun getString(key: StringResource): String {
        return StringDesc.Resource(key).localized()
    }

    fun getPlural(key: PluralsResource, quantity: Int): String {
        return StringDesc.Plural(key, quantity).localized()
    }

    object Formatted {
        fun getString(key: StringResource, vararg args: Any): String {
            return StringDesc.ResourceFormatted(key, args).localized()
        }

        fun getPlural(key: PluralsResource, quantity: Int, vararg args: Any): String {
            return StringDesc.PluralFormatted(key, quantity, args).localized()
        }
    }
}
