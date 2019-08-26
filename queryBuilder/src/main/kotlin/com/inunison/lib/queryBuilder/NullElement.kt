package com.inunison.lib.queryBuilder

/*
 * Sometimes you need nothing at all.
 */
class NullElement : Expression {

    @Override
    override fun get(): String {
        return ""
    }
}
