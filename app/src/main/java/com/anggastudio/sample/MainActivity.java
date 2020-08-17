package com.anggastudio.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.mock.Mock;
import com.anggastudio.sample.model.BitMapModel;
import com.anggastudio.sample.model.PrintBody;
import com.anggastudio.sample.model.PrintFooter;
import com.anggastudio.sample.model.PrintHeader;
import com.anggastudio.sample.model.PrintModel;
import com.anggastudio.sample.util.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> showPrinterList());
        findViewById(R.id.btn_print_text_left).setOnClickListener(v -> printTextLeft());
        findViewById(R.id.btn_print_text_center).setOnClickListener(v -> printTextCenter());
        findViewById(R.id.btn_print_text_right).setOnClickListener(v -> printTextRight());
        findViewById(R.id.btn_print_image_left).setOnClickListener(v -> printImageLeft());
        findViewById(R.id.btn_print_image_center).setOnClickListener(v -> printImageCenter());
        findViewById(R.id.btn_print_image_right).setOnClickListener(v -> printImageRight());
        findViewById(R.id.btn_print_image_ori).setOnClickListener(v -> printImageOri());
        findViewById(R.id.btn_print_image_full).setOnClickListener(v -> printImageFull());
        findViewById(R.id.btn_print_background).setOnClickListener(v -> printImageBackground());
        findViewById(R.id.btn_print_image_photo).setOnClickListener(v -> printImagePhoto());
        findViewById(R.id.btn_print_layout).setOnClickListener(v -> printView());

        getSavedPrinter();
    }

    private void getSavedPrinter() {
        BluetoothDevice connectedPrinter = Printama.with(this).getConnectedPrinter();
        if (connectedPrinter != null) {
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            connectedTo.setText("Connected to : " + connectedPrinter.getName());
        }
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

    private void showPrinterListActivity() {
        Printama.showPrinterList(this);
    }

    private void testPrinter() {
        Printama.with(this).printTest();
    }

    private void printTextLeft() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Left aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.LEFT, text);
            // or simply printama.printText("some text") --> will be printed left aligned as default
            printama.close();
        });
    }

    private void printTextCenter() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Center aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.CENTER, text);
            printama.close();
        });
    }

    private void printTextRight() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Right aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(Printama.RIGHT, text);
            printama.close();
        });
    }

    private void printImageLeft() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(Printama.LEFT, bitmap, 200);
            printama.close();
        });
    }

    private void printImageCenter() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            boolean print = printama.printImage(Printama.CENTER, bitmap, 200);
            if (!print) {
                Toast.makeText(MainActivity.this, "Print image failed", Toast.LENGTH_SHORT).show();
            }
            printama.close();
        });
    }

    private void printImageRight() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(Printama.RIGHT, bitmap, 200);
            printama.close();
        });
    }

    private void printImageOri() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap); // original size, centered as default
            printama.close();
        });
    }

    private void printImageFull() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.FULL_WIDTH);
            printama.close();
        });
    }

    private void printImageBackground() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher_background);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.ORIGINAL_WIDTH);
            printama.close();
        });
    }

    private void printImagePhoto() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rose);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.FULL_WIDTH);
            printama.close();
        });
    }

    private void printView() {
        View view = findViewById(R.id.root_view);
        Printama.with(this).connect(printama -> {
            printama.printFromView(view);
            printama.close();
        });
    }

    private void printQrReceipt() {
        PrintModel printModel = Mock.getPrintModelMock();
        Bitmap logo = Printama.getBitmapFromVector(this, R.drawable.logo_gopay_print);
        PrintHeader header = printModel.getPrintHeader();
        PrintBody body = printModel.getPrintBody();
        PrintFooter footer = printModel.getPrintFooter();
        String date = "DATE: " + body.getDate();
        String time = "PRINT TIME " + body.getTime();
        String invoice = "INVOICE: " + body.getInvoice();
        String midwareTimestamp = "CREATE TIME: " + body.getTimeStamp();

        Printama.with(this).connect(printama -> {
            printama.printImage(logo);
            printama.printText(header.getMerchantName().toUpperCase());
            printama.printText(header.getMerchantAddress1().toUpperCase());
            printama.printText(header.getMerchantAddress2().toUpperCase());
            printama.printText("MERC" + header.getMerchantId().toUpperCase());

            printama.printDoubleDashedLine();
            // body
            printama.printText(date + "   " + time);
            printama.printText(invoice + "   " + midwareTimestamp);
            printama.printDashedLine();
            printama.printText(Printama.CENTER, "TAGIHAN");
            printama.printDashedLine();
            printama.printText(Printama.CENTER, "Scan kode QR untuk membayar");

            printama.addNewLine();
            printama.printQR(body.getQrCode());
            printama.addNewLine();
            printama.printText("TOTAL         " + body.getTotalPayment());
            printama.addNewLine();
            // footer
            printama.printText(footer.getPaymentBy());
            if (footer.getIssuer() != null) printama.printText(footer.getIssuer());
            printama.printText(footer.getPowered());
            if (footer.getEnvironment() != null) printama.printText(footer.getEnvironment());
            printama.addNewLine(4);

            printama.close();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    private void showResult(String printerName) {
        Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
        TextView connectedTo = findViewById(R.id.tv_printer_info);
        connectedTo.setText("Connected to : " + printerName);
        if (!printerName.contains("failed")) {
            findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        }
    }
}
