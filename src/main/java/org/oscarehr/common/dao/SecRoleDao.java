package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.SecRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface SecRoleDao extends AbstractDao<SecRole> {

    public List<SecRole> findAll();

    public List<String> findAllNames();

    public SecRole findByName(String name);

    public List<SecRole> findAllOrderByRole();
}
