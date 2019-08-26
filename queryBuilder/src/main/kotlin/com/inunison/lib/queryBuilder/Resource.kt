package com.inunison.lib.queryBuilder

class Resource @JvmOverloads constructor(
        private val resource: String,
        private val alias: String? = null
) : Joinable {

    fun alias(): String {
        return if (this.alias == null) resource else alias
    }

    override fun get(): String {
        return if (this.alias == null)
            this.resource
        else
            String.format("%s as %s", this.resource, this.alias)
    }
}
