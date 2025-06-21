package com.anggastudio.printama_sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.constants.PA;
import com.anggastudio.printama.constants.PW;
import com.anggastudio.printama_sample.mock.Mock;
import com.anggastudio.printama_sample.model.PrintBody;
import com.anggastudio.printama_sample.model.PrintFooter;
import com.anggastudio.printama_sample.model.PrintHeader;
import com.anggastudio.printama_sample.model.PrintModel;
import com.anggastudio.printama_sample.util.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class PrintTestActivity extends AppCompatActivity {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // back button
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // print test buttons
        // simple test
        findViewById(R.id.btn_simple_print_test).setOnClickListener(v -> testPrinter());
        findViewById(R.id.btn_feed_paper).setOnClickListener(v -> feedPaper());
        // print texts
        findViewById(R.id.btn_print_text_left).setOnClickListener(v -> printTextLeft());
        findViewById(R.id.btn_print_text_center).setOnClickListener(v -> printTextCenter());
        findViewById(R.id.btn_print_text_right).setOnClickListener(v -> printTextRight());
        findViewById(R.id.btn_print_text_style).setOnClickListener(v -> printTextStyles());
        findViewById(R.id.btn_print_text_justify).setOnClickListener(v -> printTextJustified());
        // print images
        findViewById(R.id.btn_print_image_left).setOnClickListener(v -> printImageLeft());
        findViewById(R.id.btn_print_image_center).setOnClickListener(v -> printImageCenter());
        findViewById(R.id.btn_print_image_right).setOnClickListener(v -> printImageRight());
        // print images controlled width
        findViewById(R.id.btn_print_image_ori).setOnClickListener(v -> printImageOri()); // original width
        findViewById(R.id.btn_print_image_full).setOnClickListener(v -> printImageFull()); // full width
        findViewById(R.id.btn_print_image_width_1_4).setOnClickListener(v -> printImageQuarterWidth()); // 1/4 width
        findViewById(R.id.btn_print_image_width_1_2).setOnClickListener(v -> printImageHalfWidth()); // 1/2 width
        findViewById(R.id.btn_print_image_width_3_4).setOnClickListener(v -> printImageThreeQuartersWidth()); // 3/4 width
        findViewById(R.id.btn_print_image_width_1_3).setOnClickListener(v -> printImageOneThirdsWidth()); // 1/3 width
        findViewById(R.id.btn_print_image_width_2_3).setOnClickListener(v -> printImageTwoThirdWidth()); // 2/3 width
        // print image others
        findViewById(R.id.btn_print_image_photo).setOnClickListener(v -> printImagePhoto());
        findViewById(R.id.btn_print_layout).setOnClickListener(v -> printScreenLayout());
        // print receipt demo
        findViewById(R.id.btn_print_receipt).setOnClickListener(v -> printQrReceipt());
        findViewById(R.id.btn_print_receipt2).setOnClickListener(v -> printQrReceipt2());
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void testPrinter() {
        // Show loading indicator if needed
        Printama.with(this).printTest();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void feedPaper() {
        Printama.with(this).connect(printama -> {
            printama.feedPaper();
            printama.close();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printTextLeft() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Left aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(text, PA.LEFT);
            // or simply
            // printama.printText("some text") --> will be printed left aligned as default
            printama.feedPaper();
            printama.close();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printTextCenter() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Center aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(text, PA.CENTER);
            printama.feedPaper();
            printama.close();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printTextRight() {
        String text = "-------------\n" +
                "This will be printed\n" +
                "Right aligned\n" +
                "cool isn't it?\n" +
                "------------------\n";
        Printama.with(this).connect(printama -> {
            printama.printText(text, PA.RIGHT);
            printama.feedPaper();
            printama.close();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printTextJustified() {
        Printama.with(this).connect(printama -> {

            printama.printTextJustify("text1", "text2");
            printama.printTextJustify("text1", "text2", "text3");
            printama.printTextJustify("text1", "text2", "text3", "text4");

            printama.printTextJustifyBold("text1", "text2");
            printama.printTextJustifyBold("text1", "text2", "text3");
            printama.printTextJustifyBold("text1", "text2", "text3", "text4");

            printama.setNormalText();
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printTextStyles() {
        Printama.with(this).connect(printama -> {
            printama.setSmallText();
            printama.printText("small___________");
            printama.printTextln("TEXTtext");

            printama.setNormalText();
            printama.printText("normal__________");
            printama.printTextln("TEXTtext");

            printama.printTextNormal("bold____________");
            printama.printTextlnBold("TEXTtext");

            printama.setNormalText();
            printama.printTextNormal("tall____________");
            printama.printTextlnTall("TEXTtext");

            printama.printTextNormal("tall bold_______");
            printama.printTextlnTallBold("TEXTtext");

            printama.printTextNormal("wide____________");
            printama.printTextlnWide("TEXTtext");

            printama.printTextNormal("wide bold_______");
            printama.printTextlnWideBold("TEXTtext");

            printama.printTextNormal("wide tall_______");
            printama.printTextlnWideTall("TEXTtext");

            printama.printTextNormal("wide tall bold__");
            printama.printTextlnWideTallBold("TEXTtext");

            printama.printTextNormal("underline_______");
            printama.setUnderline();
            printama.printTextln("TEXTtext");

            printama.printTextNormal("delete line_____");
            printama.setDeleteLine();
            printama.printTextln("TEXTtext");

            printama.setNormalText();
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageLeft() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, 200, PA.LEFT);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageCenter() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher);
        Printama.with(this).connect(printama -> {
            boolean print = printama.printImage(bitmap, 200, PA.CENTER);
            if (!print) {
                Toast.makeText(PrintTestActivity.this, "Print image failed", Toast.LENGTH_SHORT).show();
            }
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageRight() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, 200, PA.RIGHT);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageOri() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap); // original size, centered as default
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageFull() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.FULL_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageQuarterWidth() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.QUARTER_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageHalfWidth() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.HALF_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageThreeQuartersWidth() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.THREE_QUARTERS_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageOneThirdsWidth() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.ONE_THIRD_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageTwoThirdWidth() {
        Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.TWO_THIRD_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImagePhoto() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rose);
        Printama.with(this).connect(printama -> {
            printama.printImage(bitmap, PW.FULL_WIDTH);
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printScreenLayout() {
        View view = findViewById(R.id.root_view);
        Printama.with(this).connect(printama -> {
            printama.printFromView(view);
            printama.feedPaper();
            new Handler().postDelayed(printama::close, 2000);
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printQrReceipt() {
        PrintModel printModel = Mock.getPrintModelMock();
        Bitmap logo = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        PrintHeader header = printModel.getPrintHeader();
        PrintBody body = printModel.getPrintBody();
        PrintFooter footer = printModel.getPrintFooter();
        String date = "DATE: " + body.getDate();
        String invoice = "INVOICE: " + body.getInvoice();

        Printama.with(this).connect(printama -> {
            printama.printImage(logo, 300, PA.CENTER);
            printama.addNewLine(1);
            printama.setNormalText();
            printama.printTextln(header.getMerchantName().toUpperCase(), PA.CENTER);
            printama.printTextln(header.getMerchantAddress1().toUpperCase(), PA.CENTER);
            printama.printTextln(header.getMerchantAddress2().toUpperCase(), PA.CENTER);
            printama.printTextln("MERC" + header.getMerchantId().toUpperCase(), PA.CENTER);
            printama.printDoubleDashedLine();

            // body
            printama.printTextln(date);
            printama.printTextln(invoice);

            printama.printDashedLine();
            printama.printTextln("TAGIHAN", PA.CENTER);
            printama.printDashedLine();
            printama.printTextln("Scan kode QR untuk membayar", PA.CENTER);
            printama.printImage(Util.getQrCode(body.getQrCode()), 300);
            printama.printTextln("TOTAL         " + body.getTotalPayment(), PA.CENTER);

            // footer
            printama.printTextln(footer.getPaymentBy(), PA.CENTER);
            if (footer.getIssuer() != null) printama.printText(footer.getIssuer(), PA.CENTER);
            printama.printTextln(footer.getPowered(), PA.CENTER);
            if (footer.getEnvironment() != null)
                printama.printTextln(footer.getEnvironment(), PA.CENTER);
            printama.addNewLine(4); // same as feed paper
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printQrReceipt2() {
        Bitmap logo = Printama.getBitmapFromVector(this, R.drawable.printama_logo);
        String nota = "Some Text";
        Printama.with(this).connect(printama -> {
            printama.printImage(logo, 200, PA.CENTER);
            printama.addNewLine();
            printama.printTextln("Title Text", PA.CENTER);
            printama.setNormalText();
            printama.printTextln("Some Text", PA.CENTER);
            printama.printDashedLine();
            printama.addNewLine();
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix;
            try {
                bitMatrix = writer.encode(nota, BarcodeFormat.QR_CODE, 300, 300);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = Color.WHITE;
                        if (bitMatrix.get(x, y)) color = Color.BLACK;
                        bitmap.setPixel(x, y, color);
                    }
                }
                if (bitmap != null) {
                    printama.printImage(bitmap);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }

            printama.addNewLine();
            printama.feedPaper();
            printama.close();
        }, this::showToast);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
