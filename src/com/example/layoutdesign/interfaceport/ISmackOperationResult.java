package com.example.layoutdesign.interfaceport;

import com.example.layoutdesign.Model.HttpAction;
import common.lib.http.HttpResponseParams;

public interface ISmackOperationResult {
	void onSmackExecuted (SmackActionOptions actionType, Object result );
}
