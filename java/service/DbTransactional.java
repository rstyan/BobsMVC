package com.clickability.cms.dataaccess.service;

/**
 * No methods here, but the interface is needed if you want to make
 * any methods on your class @Transactional.  This is so Guice
 * will know to check and inject the DbServiceProxy where needed.
 * 
 * @author roystyan
 *
 */
public interface DbTransactional {

}
