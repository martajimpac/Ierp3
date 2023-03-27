package com.toools.ierp.core

class Resource <T> private constructor(val status: Status, val data: T?, val exception: String?) {

    enum class Status {

        SUCCESS,
        ERROR,
        LOADING;
    }
    companion object {

        fun <T> success(data: T?): Resource<T> {

            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: String?): Resource<T> {

            return Resource(Status.ERROR, null, exception)
        }

        fun <T> loading(): Resource<T> {

            return Resource(Status.LOADING, null, null)
        }
    }
}