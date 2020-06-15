package com.anggastudio.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anggastudio.printama.Printama;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_scan).setOnClickListener(v -> scan());
        findViewById(R.id.btn_print_text_left).setOnClickListener(v -> printTextLeft());
        findViewById(R.id.btn_print_text_center).setOnClickListener(v -> printTextCenter());
        findViewById(R.id.btn_print_text_right).setOnClickListener(v -> printTextRight());
//        findViewById(R.id.btn_print_image_left).setOnClickListener(v -> printTextLeft());
//        findViewById(R.id.btn_print_image_center).setOnClickListener(v -> printTextLeft());
//        findViewById(R.id.btn_print_image_right).setOnClickListener(v -> printTextLeft());
    }

    private void scan() {
        Printama.scan(this, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
            TextView connectedTo = findViewById(R.id.tv_connected_to);
            connectedTo.setText("Connected to : " + printerName);
        });
    }

    private void printTextLeft() {
        Printama printama = new Printama(this);
        printama.connect(() -> {
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
        Printama printama = new Printama(this);
        printama.connect(() -> {
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
        Printama printama = new Printama(this);
        printama.connect(() -> {
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
        Printama printama = new Printama(this);
        printama.connect(() -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            printama.printImage(bitmap, 200);
            printama.close();
        });
    }
}
