package com.clickability.cms.dataaccess.sqlbuilder

/*
 * Sometimes you need nothing at all.
 */
class NullElement : Expression {

    @Override
    fun get(): String {
        return ""
    }

}
