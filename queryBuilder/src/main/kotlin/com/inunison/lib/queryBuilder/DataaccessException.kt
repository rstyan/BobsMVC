package com.inunison.lib.queryBuilder

/**
 * Same as SQLException except that it is Runtime so that
 * it can be used in lambda expressions.
 *
 * @author roystyan
 */
internal class DataaccessException : RuntimeException {

    constructor(cause: Exception) : super(cause) {}

    constructor(message: String) : super(message) {}

    companion object {
        private val serialVersionUID = 1L
    }
}