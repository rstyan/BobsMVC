package com.clickability.cms.dataaccess.sqlbuilder

class EmptyExpression : Expression {

    @Override
    fun get(): String {
        return "1"
    }

}
