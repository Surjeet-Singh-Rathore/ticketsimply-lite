package com.bitla.ts.utils

import androidx.annotation.StringRes

class ResourceProvider {
    sealed class TextResource {
        companion object {
            fun fromText(text : String) : TextResource = SimpleTextResource(text)
            fun fromStringId(@StringRes id : Int) : TextResource = IdTextResource(id)
        }
    }

     data class SimpleTextResource(
        val text : String
    ) : TextResource()

     data class IdTextResource(
        @StringRes val id : Int
    ) : TextResource()

//    fun asString(context: Context?): String {
//        return when (this) {
//            is SimpleTextResource -> text
//            is IdTextResource -> context?.getString(id, *args).orEmpty()
//        }
//    }
}