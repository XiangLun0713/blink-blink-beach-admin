package me.xianglun.blinkblinkbeachadmin.util

import androidx.appcompat.widget.SearchView


/**
 * Turn statement into expression
 * Can be used to turn when statement into expression so that the when statement has to be exhaustive, thus ensuring compile time safety.
 */
val <T> T.exhaustive: T
    get() = this

/**
 * Used to represent states when dealing with API
 */
sealed class APIState {
    object Loading : APIState()
    object Success : APIState()
    data class Error(val message: String? = null) : APIState()
}

/**
 * Used to represent states when dealing with API (with return value)
 */
sealed class APIStateWithValue<out T> {
    object Loading : APIStateWithValue<Nothing>()
    data class Success<out T>(val result: T) : APIStateWithValue<T>()
    data class Error(val message: String? = null) : APIStateWithValue<Nothing>()
}

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}