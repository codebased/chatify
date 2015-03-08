package common.lib.http;

import com.example.layoutdesign.Model.HttpAction;

public interface IOperationExecution {
	
	void onExecuted (HttpAction action, HttpResponseParams params);
}
