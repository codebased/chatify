package common.lib.http;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.layoutdesign.Model.HttpAction;
import common.lib.misc.ExceptionManager;

public class UnRegisterPhoneResponse extends HttpResponseParams<UnRegisterPhoneResponse> {

	public boolean hasunregistered;
	public boolean hasidentified;
	public String uniqueID;
	public IOperationExecution  operationExecution;

	@Override
	UnRegisterPhoneResponse populateData(JSONObject data) {
		try {
			this.hasunregistered = data.getBoolean("hasunregistered");
			this.hasidentified = data.getBoolean("hasidentified");
			this.uniqueID = data.getString("uniqueID");
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

