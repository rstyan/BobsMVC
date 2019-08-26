package com.clickability.cms.dataaccess.sqlbuilder

class Column : Expression {

    val name: String
    val resource: Resource?

    constructor(name: String) {
        this.name = name
        this.resource = null
    }

    constructor(resource: Resource, name: String) {
        this.name = name
        this.resource = resource
    }

    @Override
    fun get(): String {
        return if (this.resource == null)
            this.name
        else
            String.format("%s.%s", this.resource!!.alias(), this.name)
    }

}
