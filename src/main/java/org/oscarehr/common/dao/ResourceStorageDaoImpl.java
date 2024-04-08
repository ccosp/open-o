package org.oscarehr.common.dao;


import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.ResourceStorage;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceStorageDaoImpl extends AbstractDaoImpl<ResourceStorage> implements ResourceStorageDao{

	public ResourceStorageDaoImpl() {
		super(ResourceStorage.class);
	}

	@Override
	public ResourceStorage findActive(String resourceType){
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.resourceType = :resourceType AND r.active = true");
		query.setParameter("resourceType", resourceType);
		return getSingleResultOrNull(query);
	}
	
	@Override
	public List<ResourceStorage> findActiveAll(String resourceType){
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.resourceType = :resourceType AND r.active = true");
		query.setParameter("resourceType", resourceType);
		return query.getResultList();
	}

	@Override
	public List<ResourceStorage> findAll(String resourceType){
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.resourceType = :resourceType ");
		query.setParameter("resourceType", resourceType);
		return query.getResultList();
	}
	
	@Override
	public List<ResourceStorage> findByUUID(String uuid){
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.uuid = :uuid ");
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}
	
}
