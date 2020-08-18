package com.anggastudio.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.mock.Mock;
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
        findViewById(R.id.btn_print_text_style).setOnClickListener(v -> printTextStyles());
        findViewById(R.id.btn_print_image_left).setOnClickListener(v -> printImageLeft());
        findViewById(R.id.btn_print_image_center).setOnClickListener(v -> printImageCenter());
        findViewById(R.id.btn_print_image_right).setOnClickListener(v -> printImageRight());
        findViewById(R.id.btn_print_image_ori).setOnClickListener(v -> printImageOri());
        findViewById(R.id.btn_print_image_full).setOnClickListener(v -> printImageFull());
        findViewById(R.id.btn_print_background).setOnClickListener(v -> printImageBackground());
        findViewById(R.id.btn_print_image_photo).setOnClickListener(v -> printImagePhoto());
        findViewById(R.id.btn_print_layout).setOnClickListener(v -> printView());
        findViewById(R.id.btn_print_receipt).setOnClickListener(v -> printQrReceipt());

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

    private void printTextStyles() {
        Printama.with(this).connect(printama -> {
            printama.normalText();
            printama.printText("normal_____________");
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("bold_______________");
            printama.setBold();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("tall_______________");
            printama.setTall();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("tall bold__________");
            printama.setTallBold();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("wide_______________");
            printama.setWide();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("wide bold__________");
            printama.setWideBold();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("wide tall__________");
            printama.setWideTall();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("wide tall bold_____");
            printama.setWideTallBold();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("underline__________");
            printama.setUnderline();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.printText("delete line________");
            printama.setDeleteLine();
            printama.printTextln("TEXT");

            printama.normalText();
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    private void printImageLeft() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(Printama.LEFT, bitmap, 200);
            printama.close();
        }, this::showToast);
    }

    private void printImageCenter() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            boolean print = printama.printImage(Printama.CENTER, bitmap, 200);
            if (!print) {
                Toast.makeText(MainActivity.this, "Print image failed", Toast.LENGTH_SHORT).show();
            }
            printama.close();
        }, this::showToast);
    }

    private void printImageRight() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(Printama.RIGHT, bitmap, 200);
            printama.close();
        }, this::showToast);
    }

    private void printImageOri() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap); // original size, centered as default
            printama.close();
        }, this::showToast);
    }

    private void printImageFull() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.FULL_WIDTH);
            printama.close();
        }, this::showToast);
    }

    private void printImageBackground() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher_background);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.ORIGINAL_WIDTH);
            printama.close();
        }, this::showToast);
    }

    private void printImagePhoto() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rose);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, Printama.FULL_WIDTH);
            printama.close();
        }, this::showToast);
    }

    private void printView() {
        View view = findViewById(R.id.root_view);
        Printama.with(this).connect(printama -> {
            printama.printFromView(view);
            new Handler().postDelayed(printama::close, 2000);
        }, this::showToast);
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
            printama.printImage(logo, 300);
            printama.addNewLine(1);
            printama.normalText();
            printama.printTextln(Printama.CENTER, header.getMerchantName().toUpperCase());
            printama.printTextln(Printama.CENTER, header.getMerchantAddress1().toUpperCase());
            printama.printTextln(Printama.CENTER, header.getMerchantAddress2().toUpperCase());
            printama.printTextln(Printama.CENTER, "MERC" + header.getMerchantId().toUpperCase());
            printama.printDoubleDashedLine();

            // body
            printama.printTextln(date);
            printama.printTextln(invoice);

            printama.printDashedLine();
            printama.printTextln(Printama.CENTER, "TAGIHAN");
            printama.printDashedLine();
            printama.printTextln(Printama.CENTER, "Scan kode QR untuk membayar");
            printama.printImage(Util.getQrCode(body.getQrCode()), 300);
            printama.printTextln(Printama.CENTER, "TOTAL         " + body.getTotalPayment());

            // footer
            printama.printTextln(Printama.CENTER, footer.getPaymentBy());
            if (footer.getIssuer() != null) printama.printText(Printama.CENTER, footer.getIssuer());
            printama.printTextln(Printama.CENTER, footer.getPowered());
            if (footer.getEnvironment() != null)
                printama.printTextln(Printama.CENTER, footer.getEnvironment());
            printama.addNewLine(4);

            printama.close();
        }, this::showToast);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    private void showResult(String printerName) {
        showToast(printerName);
        TextView connectedTo = findViewById(R.id.tv_printer_info);
        connectedTo.setText("Connected to : " + printerName);
        if (!printerName.contains("failed")) {
            findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
