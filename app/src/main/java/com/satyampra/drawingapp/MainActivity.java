package com.satyampra.drawingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    SignatureView signatureView;
    ImageButton imgEraser, imgColor, imgPen;
    SeekBar seekBar;
    ImageView penImg;
    TextView txtPenSize;
    int defaultColor;

    private static String fileName;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myDrawings");

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signatureView = findViewById(R.id.signature_view);
        imgEraser = findViewById(R.id.btn_eraser);
        imgColor = findViewById(R.id.btn_color);
        imgPen = findViewById(R.id.btn_pen);
        penImg = findViewById(R.id.penImg);
        seekBar = findViewById(R.id.penSize);
        txtPenSize = findViewById(R.id.txtPenSize);
        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.black);
        askPermission();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = simpleDateFormat.format(new Date());
        fileName = path + "/" + date + ".jpg";

        if (!path.exists()) {
            path.mkdirs();
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtPenSize.setText(i + "dp");
                signatureView.setPenSize(i);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                penImg.setImageResource(R.drawable.ic_baseline_brush_24);
                signatureView.setPenColor(defaultColor);
                imgColor.setEnabled(true);

            }
        });

        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penImg.setImageResource(R.drawable.ic_baseline_brush_24);
                openColorPicker();
            }

        });

        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color;
                penImg.setImageResource(R.drawable.eraser);
                color = ContextCompat.getColor(MainActivity.this, R.color.white);
                signatureView.setPenColor(color);

            }
        });


    }

    private void saveImage() throws IOException {
        File file = new File(fileName);
        Bitmap bitmap = signatureView.getSignatureBitmap();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Toast.makeText(this, "Painting Saved!", Toast.LENGTH_SHORT).show();

    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                signatureView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();
    }

    private void askPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Toast.makeText(MainActivity.this, "Granted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete:
                signatureView.clearCanvas();
                Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.save:
                if (!signatureView.isBitmapEmpty()) {
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();

                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("sat", e.toString());

                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please Draw First", Toast.LENGTH_SHORT).show();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}