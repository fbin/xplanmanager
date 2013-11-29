package org.xplan.manager.web.client;

import java.util.List;

import org.xplan.manager.web.shared.Plan;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface XPlanMgrWebServiceAsync {
	void getPlans(AsyncCallback<List<Plan>> callback);
	
	void getPlan(String id, AsyncCallback<Plan> callback);
	
	void removePlan(String id, AsyncCallback<Integer> callback);
	
	void validatePlan(String id, AsyncCallback<Integer> callback);
	
	void loadPlan(String id, AsyncCallback<Integer> callback);

}
