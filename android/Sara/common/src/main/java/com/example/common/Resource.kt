package com.example.common

sealed class Resource<T>(
    val state: State = State.NONE,
    val data: T?= null,
    val message: String? = null
) {

    class Success<T>(data: T?): Resource<T>(state = State.SUCCESS, data = data)
    class Error<T>(message: String?): Resource<T>(state = State.FAIL, message = message)
    class Loading<T>: Resource<T>(state = State.ING)

}

enum class State {
    NONE, SUCCESS, FAIL, ING
}