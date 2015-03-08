package common.lib.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import common.lib.misc.ExceptionManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class JSONDataAdapter<T> extends AsyncTask<HttpRequestParams, Void, HttpResponseParams<T>>{

	private HttpResponseParams<T> response;
	private HttpRequestParams request;
	private Activity mainView = null;
	private ProgressDialog progressBar = null;

	public JSONDataAdapter(Activity view, HttpResponseParams<T> response){
		this.mainView = view;
		this.progressBar = new ProgressDialog(this.mainView);
		this.response = response;
	}

	protected void onPreExecute() {
		this.progressBar.setMessage("Please wait..");
		this.progressBar.show();
	}

	protected HttpResponseParams<T> getResult(){
		return this.response;
	}

	// Call after onPreExecute method
	protected HttpResponseParams<T> doInBackground(HttpRequestParams... params) {

		BufferedReader reader=null;

		try
		{
			this.request = params[0];
			String urlParams = "";

			if (params[0].formData != null) {

				Set<String> keys = params[0].formData.keySet();
				Iterator<String> it = keys.iterator();

				while (it.hasNext()) {
					String key = it.next();
					urlParams +="&" + URLEncoder.encode(key, "UTF-8") + "="+params[0].formData.get(key).toString();
				}
			}

			// Send request.
			URL url = new URL(params[0].httpAction.Url);
			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true); 
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			wr.write( urlParams ); 
			wr.flush(); 

			// Get the server response 
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			// Read Server Response
			while((line = reader.readLine()) != null)
			{
				// Append server response in string
				sb.append(line + "");
			}

			// Append Server Response To Content String 
			this.response.serverResponse  = sb.toString();
			this.response.httpAction = this.request.httpAction;
		}
		catch(Exception ex)
		{
			this.response.serverError = ex.getMessage();

		}
		finally
		{
			try
			{
				reader.close();
			}

			catch(Exception ex) {ExceptionManager.handleException(ex);
			}
		}

		return this.response;
	}

	@Override
	protected void onPostExecute(HttpResponseParams<T> result) {

		this.response = result;

		if ( this.response != null ) {
			
			this.progressBar.dismiss();

			if (this.response.serverError != null) {
				this.response.serverResponse = this.response.serverError;

			} else {

				try {

					this.response.json = new JSONObject(this.response.serverResponse);
					this.response.populateData(this.response.json);
					//					JSONArray jsonMainNode = this.response.json.optJSONArray("Result");
					//
					//					for(int idx=0; idx < jsonMainNode.length(); idx++) 
					//					{
					//						JSONObject jsonChildNode = jsonMainNode.getJSONObject(idx);
					//						this.response.jsonData.add(this.response.populateData(jsonChildNode));
					//					}
				} catch (JSONException e) {
					ExceptionManager.handleException(e);
				}
			}
		}

		this.response.onExecuted(this.response.httpAction);
	}
}

