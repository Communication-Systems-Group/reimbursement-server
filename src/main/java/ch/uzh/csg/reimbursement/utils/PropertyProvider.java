package ch.uzh.csg.reimbursement.utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import ch.uzh.csg.reimbursement.model.exception.ServiceException;

public enum PropertyProvider {

	INSTANCE;

	private final Logger LOG = LoggerFactory.getLogger(PropertyProvider.class);
	private Resource resource = new ClassPathResource("application.properties");
	private Properties props;

	private PropertyProvider(){
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			LOG.error("An IOException has been caught while readubg the properties for a user.", e);
			throw new ServiceException();
		}
	}

	public String getProperty(String propertyName){
		return props.getProperty(propertyName);
	}
}
