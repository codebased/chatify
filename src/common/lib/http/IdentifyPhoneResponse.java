package common.lib.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.layoutdesign.Model.HttpAction;

import common.lib.misc.ExceptionManager;

public class IdentifyPhoneResponse extends HttpResponseParams<IdentifyPhoneResponse> {

	public boolean hassentSMS;
	public boolean hasidentified;
	public String authenticationCode;
	
	public IOperationExecution operationExecution;

	@Override
	IdentifyPhoneResponse populateData(JSONObject data) {
		try {
			this.hassentSMS = data.getBoolean("hassentSMS");
			this.hasidentified = data.getBoolean("hasidentified");
			this.authenticationCode  = data.getString("authenticationCode");
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
