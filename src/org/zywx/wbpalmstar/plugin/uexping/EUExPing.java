package org.zywx.wbpalmstar.plugin.uexping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

import android.content.Context;
import android.util.Log;

public class EUExPing extends EUExBase {

	public static final String CALLBACK_PING = "uexPing.onStart";
	public static final String TAG = "EUExPing";
	private String jsonstr;
	private String resultBack;

	public EUExPing(Context context, EBrowserView view) {
		super(context, view);
	}

	public void start(String[] params) {
		//Log.i(TAG, "doPing");
		if (params.length < 1) {
			return;
		} else {
			jsonstr = params[0];
			//Log.d("jsonstr", jsonstr);
			try {

				JSONArray array = new JSONArray(jsonstr);
				int arraySize = array.length();
				Log.e("jsonSize", arraySize + "");
				for (int i = 0; i < arraySize; i++) {
					final String str = array.getString(i);
					//Log.i("Str", str);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							resultBack = ping(str);
							Log.d("result", resultBack);
							jsCallback(CALLBACK_PING, 0, EUExCallback.F_C_JSON, resultBack);
						}
					}).start();

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private String ping(String url) {

		@SuppressWarnings("unused")
		String result = null;
		String jsStr = null;
		try {

			String ip = url;

			Process p = Runtime.getRuntime().exec("ping -c 3 " + ip);// ping3次

			InputStream input = p.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			StringBuffer stringBuffer = new StringBuffer();

			String content = "";

			while ((content = in.readLine()) != null) {

				stringBuffer.append(content);

			}

			//Log.i("TTT", "result content : " + stringBuffer.toString());
			// PING的状态

			int status = p.waitFor();

			if (status == 0) {

				result = "successful~";
				String str = stringBuffer.toString();
				String strResult[] = str.split(" ");
				String strSuccess = strResult[strResult.length - 2];
				String strSuccessResult[] = strSuccess.split("/");
				String min = strSuccessResult[0];
				String avg = strSuccessResult[1];
				String max = strSuccessResult[2];
				//Log.e("TheResult", "min:" + min + " avg:" + avg + " max:" + max);
				jsStr = "{\"status\"" + ":" + "\"" + status + "\"" + ","
						+ "\"addr\"" + ":" + "\"" + ip + "\"" + "," + "\"avg\""
						+ ":" + "\"" + avg + "\"" + "," + "\"min\"" + ":"
						+ "\"" + min + "\"" + "," + "\"max\"" + ":" + "\""
						+ max + "\"" + "}";
				//Log.d("JSON-->", jsStr);
			} else {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("status", "-1");
					jsonObject.put("addr", ip);
					jsonObject.put("avg", "0");
					jsonObject.put("min", "0");
					jsonObject.put("max", "0");
					jsStr = jsonObject.toString();
					//Log.d("JSON-->", jsStr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				result = "failed~ cannot reach the IP address";

			}

		} catch (IOException e) {

			result = "failed~ IOException";

		} catch (InterruptedException e) {
			result = "failed~ InterruptedException";

		} finally {

			//Log.i("TTT", "result = " + result);

		}

		return jsStr;

	}

	@Override
	protected boolean clean() {
		return false;
	}

}
