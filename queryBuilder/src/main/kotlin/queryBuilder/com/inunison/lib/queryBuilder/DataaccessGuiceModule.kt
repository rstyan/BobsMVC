package com.clickability.cms.dataaccess

import com.clickability.cms.dataaccess.customer.CustomerDao
import com.clickability.cms.dataaccess.customer.CustomerDaoImpl
import com.clickability.cms.dataaccess.customer.UserDao
import com.clickability.cms.dataaccess.customer.UserDaoImpl
import com.clickability.cms.dataaccess.domain.IPRangeDao
import com.clickability.cms.dataaccess.domain.IPRangeDaoImpl
import com.clickability.cms.dataaccess.service.DbServiceProxy
import com.clickability.cms.dataaccess.service.DbTransactional
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbTransaction
import com.clickability.cms.dataaccess.template.MetadataDao
import com.clickability.cms.dataaccess.template.MetadataDaoImpl
import com.clickability.cms.dataaccess.template.TemplateFolderDao
import com.clickability.cms.dataaccess.template.TemplateFolderDaoImpl
import com.clickability.cms.type.ContentWorkflowDefaultDao
import com.clickability.cms.type.ContentWorkflowDefaultDaoImpl
import com.clickability.cms.type.DesignItemDao
import com.clickability.cms.type.DesignItemDaoImpl
import com.clickability.cms.type.DesignItemFactory
import com.clickability.cms.type.DesignItemFactoryImpl
import com.clickability.security.GroupDao
import com.clickability.security.GroupDaoImpl
import com.clickability.security.UserGroupDao
import com.clickability.security.UserGroupDaoImpl
import com.google.inject.AbstractModule
import com.google.inject.matcher.Matchers

// FIXME Move these to a more functional organization
// i.e. domain tables go in the DomainGuiceModule,
// orgainization tables to in the OrganizationModule etc.
class DataaccessGuiceModule : AbstractModule() {

    fun configure() {
        bind(DesignItemDao::class.java).to(DesignItemDaoImpl::class.java)
        bind(DesignItemFactory::class.java).to(DesignItemFactoryImpl::class.java)
        bind(CustomerDao::class.java).to(CustomerDaoImpl::class.java)
        bind(UserDao::class.java).to(UserDaoImpl::class.java)
        bind(GroupDao::class.java).to(GroupDaoImpl::class.java)
        bind(UserGroupDao::class.java).to(UserGroupDaoImpl::class.java)
        bind(ContentWorkflowDefaultDao::class.java).to(ContentWorkflowDefaultDaoImpl::class.java)
        bind(TemplateFolderDao::class.java).to(TemplateFolderDaoImpl::class.java)
        bind(MetadataDao::class.java).to(MetadataDaoImpl::class.java)
        bind(IPRangeDao::class.java).to(IPRangeDaoImpl::class.java)

        // Implements Transactional processing on the dataaccess classes
        val dbServiceProxy = DbServiceProxy()
        bindInterceptor(Matchers.subclassesOf(DbTransactional::class.java), Matchers.annotatedWith(DbTransaction::class.java), dbServiceProxy)
    }
}
