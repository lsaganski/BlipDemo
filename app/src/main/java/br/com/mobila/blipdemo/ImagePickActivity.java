package br.com.mobila.blipdemo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;

import livroandroid.lib.utils.ImageResizeUtils;
import livroandroid.lib.utils.SDCardUtils;

public class ImagePickActivity extends Activity {

    private File file;
    private ImageView imageView;
    private TextView lblResult;

    Handler handlerLogin = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                Utils.Show("Erro na autenticação", true);
            else {
                Utils.Show("AUTENTICADO !", true);
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                Utils.Show("Erro ao enviar imagem", true);
            else {
                Utils.Show("Deu certo", true);
                lblResult.setText(Globals.getInstance().resp_displayname);
               /* Log.d("Retorno", Globals.getInstance().resp_displayname);
                Log.d("Retorno", Globals.getInstance().resp_matchtypes);
                Log.d("Retorno", Globals.getInstance().resp_name);
                Log.d("Retorno", Globals.getInstance().resp_passparams);
                Log.d("Retorno", Globals.getInstance().resp_score);
                Log.d("Retorno", Globals.getInstance().resp_thumbnailurl);*/
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);

        Globals.getInstance().applicationContext = getApplicationContext();

        imageView = (ImageView) findViewById(R.id.result);
        lblResult = (TextView) findViewById(R.id.lblResult);

        Api.getInstance().Login(handlerLogin);

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
            showImage(file);
        }
    }

    public void onClick(View View) {
        file = SDCardUtils.getPrivateFile(getBaseContext(), "foto.jpg", Environment.DIRECTORY_PICTURES);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 //       intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("file", file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && file != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

         //   Uri selectedImage = data.getData();

         //   int orientation = getOrientation(getBaseContext(), selectedImage);

            thumbnail = HandleBitmapPhoto(200, 200, thumbnail, 0); //orientation
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            Globals.getInstance().selectedPhoto = bytes.toByteArray();

            Api.getInstance().SendImage(handler);
        }

    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Bitmap HandleBitmapPhoto(int w, int h, Bitmap b, int orientation) {
        boolean portrait = true;
        double root = 1;
        int croppedSize = 0;

        if(orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                    b.getHeight(), matrix, true);
        }

        if (b.getWidth() > b.getHeight())
            portrait = false;

        if (portrait) {
            root = ((double)b.getHeight()) / ((double)b.getWidth());

            b = Bitmap.createScaledBitmap(b, w, ((int)(w*root)), false);

            if (b.getHeight() > h) {
                croppedSize = (b.getHeight() - h) / 2;

                b = Bitmap.createBitmap(b, 0, croppedSize, w, h);
            }
        } else {
            root = ((double)b.getWidth()) / ((double)b.getHeight());

            b = Bitmap.createScaledBitmap(b, ((int)(h*root)), h, false);

            if (b.getWidth() > w) {
                croppedSize = (b.getWidth() - w) / 2;

                b = Bitmap.createBitmap(b, croppedSize, 0, w, h);
            }
        }

        return b;
    }

    private void showImage(File file) {
        if (file != null && file.exists()) {
            Log.d("foto", file.getAbsolutePath());

            int w = imageView.getWidth();
            int h = imageView.getHeight();

            Bitmap bitmap = ImageResizeUtils.getResizedImage(Uri.fromFile(file), w, h, false);
            Toast.makeText(this, "w/h:" + imageView.getWidth() + "/" + imageView.getHeight() + " > " + "w/h" + bitmap.getWidth() + "/" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();;
            imageView.setImageBitmap(bitmap);
        }
    }
}
