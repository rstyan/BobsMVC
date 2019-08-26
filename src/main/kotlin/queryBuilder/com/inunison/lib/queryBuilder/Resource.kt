package com.clickability.cms.dataaccess.sqlbuilder

class Resource @JvmOverloads constructor(val resource: String, val alias: String? = null) : Joinable {

    fun alias(): String {
        return if (this.alias == null) resource else alias
    }

    @Override
    fun get(): String {
        return if (this.alias == null)
            this.resource
        else
            String.format("%s as %s", this.resource, this.alias)
    }

}
