package com.bitla.restaurant_app.presentation.utils

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set   // Allow external read but not write

    // Returns the content and prevents if already handled
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    // Returns the content even if it is already handled
    fun peekContent(): T = content
}