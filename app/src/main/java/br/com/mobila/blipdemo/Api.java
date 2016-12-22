package br.com.mobila.blipdemo;

/**
 * Created by Leonardo Saganski on 13/12/16.
 */

import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Api implements Response.ErrorListener {

    JsonObjectRequest jsonObjReq;
    JsonArrayRequest jsonArrayReq;
    int what;
    Gson gson;
    Handler handler;

    boolean ins;

    String apiPath = "http://www.mobila.kinghost.net/aidavecapi/api/";



    private static Api instance;

    public static Api getInstance() {
        if (instance == null)
            instance = new Api();

        return instance;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        if (error != null) {
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                Utils.Show("Timeout Error: " + error.getMessage(), true);
            } else if (error instanceof AuthFailureError) {
                Utils.Show("Auth Error: " + error.getMessage(), true);
            } else if (error instanceof ServerError) {
                Utils.Show("Server Error: " + error.getMessage(), true);
            } else if (error instanceof NetworkError) {
                Utils.Show("Network Error: " + error.getMessage(), true);
            } else if (error instanceof ParseError) {
                Utils.Show("Parse Error: " + error.getMessage(), true);
            }

            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 404) {
                    Utils.Show("404 Error : " + error.getMessage(), true);
                }
            }
        } else {
            Utils.Show("Error : " + error.getMessage(), true);
        }

        handler.sendEmptyMessage(0);

    }

    public void Login(Handler h) {
        try {
            handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = "https://bauth.blippar.com/token?grant_type=client_credentials&client_id=d708593117314d8b8ce08b253f42e1cd&client_secret=07b24229feb1493dbbf2ff6b5804e314";
                int verb = Request.Method.GET;

                jsonObjReq = new JsonObjectRequest(verb, path, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("token_type") && response.has("access_token")) {
                                        Globals.getInstance().tokenType = response.get("token_type").toString();
                                        Globals.getInstance().accessToken = response.get("access_token").toString();

                                        handler.sendEmptyMessage(1);
                                    } else  {
                                        handler.sendEmptyMessage(0);
                                    }
                                } catch (JSONException e) {
                                    Utils.Show("API Error (JSon) Login : " + e.getMessage(), true);
                                }
                            }
                        }, this);

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(jsonObjReq, "tag");
            }
        } catch (Exception e) {
            Utils.Show("API Error Login : " + e.getMessage(), true);
        }
    }

    public void SendImage(Handler h) {
        try {
            handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = "https://bapi.blippar.com/v1/imageLookup";

                int verb = Request.Method.POST;

                final Gson gson = new Gson();

                PhotoMultipartRequest req = new PhotoMultipartRequest(path, this,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    BlipData[] d = gson.fromJson(response, BlipData[].class);

                                    Globals.getInstance().resp_displayname = "";

                                    if (d != null) {
                                        for (BlipData b : d) {
                                            Globals.getInstance().resp_displayname += b.getDisplayName() + "\n";
                                        }
                                    }

  /*                                  if (response.has("id"))
                                        Globals.getInstance().resp_ID = response.get("id").toString();

                                    if (response.has("name"))
                                        Globals.getInstance().resp_name = response.get("name").toString();

                                    if (response.has("displayname"))
                                        Globals.getInstance().resp_displayname = response.get("displayname").toString();

                                    if (response.has("thumbnailurl"))
                                        Globals.getInstance().resp_thumbnailurl = response.get("thumbnailurl").toString();

                                    if (response.has("matchtypes"))
                                        Globals.getInstance().resp_matchtypes = response.get("matchtypes").toString();

                                    if (response.has("score"))
                                        Globals.getInstance().resp_score = response.get("score").toString();

                                    if (response.has("passparams"))
                                        Globals.getInstance().resp_passparams = response.get("passparams").toString();
*/

                                    handler.sendEmptyMessage(1);

                                } catch (Exception e) {
                                    handler.sendEmptyMessage(0);
                                    Utils.Show("API Error (JSon) Login : " + e.getMessage(), true);
                                }
                            }
                        }, (byte[]) Globals.getInstance().selectedPhoto){

                  /*  @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();

                        headers.put("Authorization", Globals.getInstance().tokenType + " " + Globals.getInstance().accessToken);

                        return headers;
                    }*/
                };

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.Show("API Error SendImage : " + e.getMessage(), true);
        }
    }



}
