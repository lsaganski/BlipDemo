package br.com.mobila.blipdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;

import livroandroid.lib.utils.ImageResizeUtils;
import livroandroid.lib.utils.SDCardUtils;

public class ImagePickActivity extends Activity {

    private static final String TAG = "MainActivity";

    private File file;
    private ImageView imgResult;
    private br.com.mobila.blipdemo.TagCloudView lblResult;
    private ProgressBar prbProgress;
    private Button btnOk;
    private LinearLayout layMaster;

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
            if (msg.what == 0) {
                Utils.Show("Erro ao enviar imagem", true);
                prbProgress.setVisibility(View.GONE);
                lblResult.setVisibility(View.GONE);
            } else {
                Utils.Show("Deu certo", true);
                lblResult.setVisibility(View.VISIBLE);
                prbProgress.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);

        Globals.getInstance().applicationContext = getApplicationContext();

        layMaster = (LinearLayout) findViewById(R.id.layMaster);
        imgResult = (ImageView) findViewById(R.id.imgResult);
        lblResult = (TagCloudView) findViewById(R.id.lblResult);
        prbProgress = (ProgressBar) findViewById(R.id.prbProgress);
        btnOk = (Button) findViewById(R.id.btnOk);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkForPermission();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    file = SDCardUtils.getPrivateFile(getBaseContext(), "foto.jpg", Environment.DIRECTORY_PICTURES);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //       intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, 0);
                } else {
                    checkForPermission();
                }
            }
        });

        Api.getInstance().Login(handlerLogin);

        if (savedInstanceState != null) {
            file = (File) savedInstanceState.getSerializable("file");
            showImage(file);
        }

        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        if (Globals.getInstance().tags.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lblResult.scroll -= 100;
                                    lblResult.invalidate();
                                }
                            });
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        thread.start();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        int permissionCheck = checkSelfPermission(Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Granted");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImagePickActivity.this, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Contacts Permission Required!!");
                createSnackbar("Contacts Permission Required!!", "Try Again");
            }
            ActivityCompat.
                    requestPermissions(ImagePickActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        }
    }

    private void createSnackbar(String message, String action) {
        Snackbar
                .make(layMaster, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.
                                requestPermissions(ImagePickActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, 1);
                    }
                })
                .show();
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

            thumbnail = HandleBitmapPhoto(300, 300, thumbnail, 0); //orientation
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            byte[] bb = bytes.toByteArray();
            Globals.getInstance().selectedPhoto = bb;

            imgResult.setImageBitmap(thumbnail);

            lblResult.setVisibility(View.GONE);
            prbProgress.setVisibility(View.VISIBLE);

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

            int w = imgResult.getWidth();
            int h = imgResult.getHeight();

            Bitmap bitmap = ImageResizeUtils.getResizedImage(Uri.fromFile(file), w, h, false);
            Toast.makeText(this, "w/h:" + imgResult.getWidth() + "/" + imgResult.getHeight() + " > " + "w/h" + bitmap.getWidth() + "/" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();;
            imgResult.setImageBitmap(bitmap);
        }
    }
}
