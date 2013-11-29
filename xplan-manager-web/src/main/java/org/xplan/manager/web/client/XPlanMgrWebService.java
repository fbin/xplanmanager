package org.xplan.manager.web.client;

import java.util.List;

import org.xplan.manager.web.shared.Plan;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface XPlanMgrWebService extends RemoteService {
	List<Plan> getPlans();
	
	Plan getPlan(String id);
	
	int removePlan(String id);
	
	int validatePlan(String id);
	
	int loadPlan(String id);
	
}
