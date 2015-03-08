package common.lib.http;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.layoutdesign.Model.HttpAction;
import common.lib.misc.ExceptionManager;

public class AuthenticationResponse extends HttpResponseParams<AuthenticationResponse> {

	public boolean authenticated;
	public String phoneNumber;
	public IOperationExecution  operationExecution;

	@Override
	AuthenticationResponse populateData(JSONObject data) {
		try {
			this.authenticated = data.getBoolean("authenticated");
			this.phoneNumber = data.getString("phoneNumber");
			return this;
		} catch (JSONException e) {
			ExceptionManager.handleException(e);
		}

		return null;
	}

	@Override
	void onExecuted(HttpAction action) {
		if ( operationExecution != null ){
			this.operationExecution.onExecuted(action, this);
		}
	}
}
