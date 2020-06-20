package com.anggastudio.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.anggastudio.printama.Printama;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_choose_printer).setOnClickListener(v -> showPrinterList());
        findViewById(R.id.btn_print_text_left).setOnClickListener(v -> printTextLeft());
        findViewById(R.id.btn_print_text_center).setOnClickListener(v -> printTextCenter());
        findViewById(R.id.btn_print_text_right).setOnClickListener(v -> printTextRight());
        findViewById(R.id.btn_print_image_left).setOnClickListener(v -> printImageLeft());
        findViewById(R.id.btn_print_image_center).setOnClickListener(v -> printImageCenter());
        findViewById(R.id.btn_print_image_right).setOnClickListener(v -> printImageRight());
        findViewById(R.id.btn_print_image_ori).setOnClickListener(v -> printImageOri());
        findViewById(R.id.btn_print_image_full).setOnClickListener(v -> printImageFull());
    }

    private void showPrinterList() {
        Printama.showPrinterList(this, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            connectedTo.setText("Connected to : " + printerName);
            if (!printerName.contains("failed")) {
                findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
            }
        });
    }

    private void testPrinter() {
        Printama.with(this).printTest();
    }

    private void printTextLeft() {
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.LEFT,
                    "-------------\n" +
                            "This will be printed\n" +
                            "Left aligned\n" +
                            "cool isn't it?\n" +
                            "------------------\n");
            // or simply printama.printText("some text") --> will be printed left aligned as default
            printama.close();
        });
    }

    private void printTextCenter() {
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.CENTER,
                    "-------------\n" +
                            "This will be printed\n" +
                            "Center aligned\n" +
                            "cool isn't it?\n" +
                            "------------------\n");
            printama.close();
        });
    }

    private void printTextRight() {
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.RIGHT,
                    "-------------\n" +
                            "This will be printed\n" +
                            "Right aligned\n" +
                            "cool isn't it?\n" +
                            "------------------\n");
            printama.close();
        });
    }

    private void printImageLeft() {
        Printama.with(this).connect(printama -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
            printama.printImage(Printama.LEFT, bitmap, 200);
            printama.close();
        });
    }

    private void printImageCenter() {
        Printama.with(this).connect(printama -> {
            Bitmap bitmap = getBitmapFromVector(MainActivity.this, R.drawable.ic_launcher_background);
            boolean print = printama.printImage(Printama.CENTER, bitmap, 200);
            if (!print) {
                Toast.makeText(MainActivity.this, "Print image failed", Toast.LENGTH_SHORT).show();
            }
            printama.close();
        });
    }

    private void printImageRight() {
        Printama.with(this).connect(printama -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            printama.printImage(Printama.RIGHT, bitmap, 200);
            printama.close();
        });
    }

    private void printImageOri() {
        Printama.with(this).connect(printama -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            printama.printImage(bitmap); // original size, centered as default
            printama.close();
        });
    }

    private void printImageFull() {
        Printama.with(this).connect(printama -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            printama.printImage(bitmap, Printama.FULL_WIDTH);
            printama.close();
        });
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
