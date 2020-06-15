package com.anggastudio.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anggastudio.printama.Printama;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_scan).setOnClickListener(v -> scan());
        findViewById(R.id.btn_print).setOnClickListener(v -> print());
    }

    private void scan() {
        Printama.scan(this, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
        });
    }

    private void print() {
        Printama printama = new Printama();
        printama.connect(() -> {
            printama.printText(Printama.CENTER, "-------------\n" +
                    "Coming soon\n" +
                    "another anggastudio\n" +
                    "android library\n" +
                    "for bluetooth thermal printer\n" +
                    "------------------\n");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            printama.printImage(bitmap, 200);
            printama.close();
        });
    }
}
