package br.com.mobila.blipdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;


public class PhotoMultipartRequest<T> extends Request<T> {

    private static final String FILE_PART_NAME = "file";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Listener<T> mListener;
    private final byte[] mImageFile;
    protected Map<String, String> headers;

    public PhotoMultipartRequest(String url,
                                 ErrorListener errorListener,
                                 Listener<T> listener,
                                 byte[] imageFile)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mImageFile = imageFile;

        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Authorization", "Token(" + Globals.getInstance().tokenType + " " + Globals.getInstance().accessToken + ")");
        headers.put("LatLong", "");
        headers.put("LatLongAccuracy", "");
        headers.put("Language", "pt-BR");
        headers.put("DeviceOS", "");
        headers.put("DeviceType", "");
        headers.put("DeviceVersion", "");
        headers.put("UniqueID", "");
        headers.put("Accelerometer", "");
        headers.put("DeviceOrientation", "0");
        headers.put("Gyro", "");

//        headers.put("Accept", "application/json");

        return headers;
    }

    private void buildMultipartEntity()
    {
       /* mBuilder.addBinaryBody(FILE_PART_NAME, mImageFile, ContentType.create("image/jpg"), "uploadPhoto");
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));*/

        mBuilder.addBinaryBody(FILE_PART_NAME, mImageFile, ContentType.create("image/jpeg"), "uploadPhoto");
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType()
    {

        //return "application/json; charset=utf-8";
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            mBuilder.build().writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try {
            String result = null;
            result = new String( response.data, HttpHeaderParser.parseCharset( response.headers ) );
            return ( Response<T> ) Response.success( new String( result ), HttpHeaderParser.parseCacheHeaders(response) );
        } catch ( UnsupportedEncodingException e ) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response)
    {
        mListener.onResponse(response);
    }
}