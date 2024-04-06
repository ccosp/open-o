package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.oscarehr.common.model.OcanStaffForm;

@Repository
public class OcanStaffFormDaoImpl extends AbstractDaoImpl<OcanStaffForm> implements OcanStaffFormDao {

	public OcanStaffFormDaoImpl() {
		super(OcanStaffForm.class);
	}
	
	//... rest of the methods implementation goes here
}
