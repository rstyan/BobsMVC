package com.clickability.cms.dataaccess;

import com.clickability.cms.dataaccess.customer.CustomerDao;
import com.clickability.cms.dataaccess.customer.CustomerDaoImpl;
import com.clickability.cms.dataaccess.customer.UserDao;
import com.clickability.cms.dataaccess.customer.UserDaoImpl;
import com.clickability.cms.dataaccess.domain.IPRangeDao;
import com.clickability.cms.dataaccess.domain.IPRangeDaoImpl;
import com.clickability.cms.dataaccess.service.DbServiceProxy;
import com.clickability.cms.dataaccess.service.DbTransactional;
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbTransaction;
import com.clickability.cms.dataaccess.template.MetadataDao;
import com.clickability.cms.dataaccess.template.MetadataDaoImpl;
import com.clickability.cms.dataaccess.template.TemplateFolderDao;
import com.clickability.cms.dataaccess.template.TemplateFolderDaoImpl;
import com.clickability.cms.type.ContentWorkflowDefaultDao;
import com.clickability.cms.type.ContentWorkflowDefaultDaoImpl;
import com.clickability.cms.type.DesignItemDao;
import com.clickability.cms.type.DesignItemDaoImpl;
import com.clickability.cms.type.DesignItemFactory;
import com.clickability.cms.type.DesignItemFactoryImpl;
import com.clickability.security.GroupDao;
import com.clickability.security.GroupDaoImpl;
import com.clickability.security.UserGroupDao;
import com.clickability.security.UserGroupDaoImpl;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

// FIXME Move these to a more functional organization
// i.e. domain tables go in the DomainGuiceModule,
// orgainization tables to in the OrganizationModule etc.
public class DataaccessGuiceModule extends AbstractModule {

    public void configure() {
		bind(DesignItemDao.class).to(DesignItemDaoImpl.class);
		bind(DesignItemFactory.class).to(DesignItemFactoryImpl.class);
		bind(CustomerDao.class).to(CustomerDaoImpl.class);
		bind(UserDao.class).to(UserDaoImpl.class);
		bind(GroupDao.class).to(GroupDaoImpl.class);
		bind(UserGroupDao.class).to(UserGroupDaoImpl.class);
		bind(ContentWorkflowDefaultDao.class).to(ContentWorkflowDefaultDaoImpl.class);
		bind(TemplateFolderDao.class).to(TemplateFolderDaoImpl.class);
		bind(MetadataDao.class).to(MetadataDaoImpl.class);
		bind(IPRangeDao.class).to(IPRangeDaoImpl.class);
		
		// Implements Transactional processing on the dataaccess classes
		DbServiceProxy dbServiceProxy = new DbServiceProxy();
		bindInterceptor(Matchers.subclassesOf(DbTransactional.class), Matchers.annotatedWith(DbTransaction.class), dbServiceProxy);
   }
}
