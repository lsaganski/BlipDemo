package br.com.mobila.blipdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Utils {

    boolean mResult;



    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();

        return instance;
    }

    public String getStringNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date rawDate = Calendar.getInstance().getTime();
        String formattedDate = df.format(rawDate);

        return formattedDate;
    }

    public static void Show(String msg, boolean longToast){
        Toast t = Toast.makeText(Globals.getInstance().applicationContext, msg, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
        t.show();
    }

    public static boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) Globals.getInstance().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public boolean getYesNoWithExecutionStop(String title, String message,
                                             Context context) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public boolean getYesNoConfirmWithExecutionStop(String title, String message, String Sim, String Nao,
                                                    Context context) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(Nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton(Sim, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public static String DefStrVal(String valor, String defaultValue){
        if (valor == null)
            return defaultValue;
        else
            return valor;
    }

    public static String HashMD5(String s) {
        String res = "";

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            res = new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {

        }

        return res;
    }

    public static String CleanStr(String s){
        return s.toUpperCase()
                .replace('Ã','A')
                .replace('Á','A')
                .replace('Â','A')
                .replace('À','A')
                .replace('É','E')
                .replace('Õ','O')
                .replace('Ó','O')
                .replace('Ô','O')
                .replace('Í','I')
                .replace('Ú','U')
                .replace('Ç','C');
    }

    public static String MaskPhone(String v)
    {
        if (v.length() > 0 && v.charAt(0) != '(')
            v = '(' + v;

        if (v.length() > 3 && v.charAt(3) != ')')
            v = v.substring(0, 2) + ')' + v.substring(3, v.length()-1);

        return v;
    }

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
