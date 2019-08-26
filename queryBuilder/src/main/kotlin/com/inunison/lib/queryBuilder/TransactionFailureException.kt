package com.inunison.lib.queryBuilder

internal class TransactionFailureException(root: Throwable) : Exception(root) {
    companion object {
        private val serialVersionUID = 1L
    }
}
