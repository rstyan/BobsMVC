package com.clickability.cms.dataaccess.sqlbuilder

class TransactionFailureException(root: Throwable) : Exception(root) {
    companion object {

        private val serialVersionUID = 1L
    }

}
