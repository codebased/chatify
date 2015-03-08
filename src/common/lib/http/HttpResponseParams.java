package common.lib.http;

import java.util.ArrayList;

import org.json.JSONObject;

import com.example.layoutdesign.Model.HttpAction;
import com.example.layoutdesign.Model.HttpActionOptions;

public abstract class HttpResponseParams<T> {
	public String serverResponse;
	public HttpAction httpAction;
	public String serverError;
	public JSONObject json;
	ArrayList<T> jsonData;
	abstract T populateData(JSONObject data);
	abstract void onExecuted(HttpAction action);
}
