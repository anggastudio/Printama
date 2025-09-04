package com.anggastudio.printama_sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
        findViewById(R.id.btn_print_receipt3).setOnClickListener(v -> printThreeColumnReceipt());
        findViewById(R.id.btn_print_receipt4).setOnClickListener(v -> printFourColumnReceipt());

        // others
        findViewById(R.id.btn_print_exmpl1).setOnClickListener(v -> printExample1());
        findViewById(R.id.btn_print_exmpl2).setOnClickListener(v -> printExample2());
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
        String qrData = "https://printama.anggastudio.dev/payment/12345";

        Printama.with(this).connect(printama -> {
            // Header Section
            printama.printImage(logo, PW.QUARTER_WIDTH, PA.CENTER);
            printama.addNewLine();
            printama.setBold();
            printama.printTextln("DIGITAL RECEIPT", PA.CENTER);
            printama.setNormalText();
            printama.printTextln("Coffee Shop Express", PA.CENTER);
            printama.printTextln("123 Main Street, City", PA.CENTER);
            printama.printTextln("Tel: (555) 123-4567", PA.CENTER);
            printama.printDoubleDashedLine();

            // Transaction Details
            printama.printTextJustify("Receipt #:", "RCP-001234");
            printama.printTextJustify("Date:", "2024-01-15 14:30");
            printama.printTextJustify("Cashier:", "John Doe");
            printama.printDashedLine();

            // Items Section
            printama.setBold();
            printama.printTextln("ITEMS PURCHASED", PA.CENTER);
            printama.setNormalText();
            printama.printDashedLine();
            printama.printTextJustify("Espresso x2", "$6.00");
            printama.printTextJustify("Croissant x1", "$3.50");
            printama.printTextJustify("Latte x1", "$4.50");
            printama.printDashedLine();
            printama.printTextJustify("Subtotal:", "$14.00");
            printama.printTextJustify("Tax (8%):", "$1.12");
            printama.setBold();
            printama.printTextJustify("TOTAL:", "$15.12");
            printama.setNormalText();
            printama.printDoubleDashedLine();

            // Payment Section
            printama.setBold();
            printama.printTextln("PAYMENT METHOD", PA.CENTER);
            printama.setNormalText();
            printama.printTextln("Scan QR Code to Pay", PA.CENTER);

            // Reduce line spacing before QR code
            printama.setLineSpacing(0);

            // QR Code Generation
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 250, 250);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                printama.printImage(qrBitmap, PA.CENTER);
            } catch (WriterException e) {
                e.printStackTrace();
                printama.printTextln("QR Code generation failed", PA.CENTER);
            }

            // Reset line spacing to normal
            printama.setLineSpacing(30);

            printama.printTextln("Payment Link:", PA.CENTER);
            printama.setSmallText();
            printama.printTextln(qrData, PA.CENTER);
            printama.setNormalText();
            printama.printDoubleDashedLine();

            // Footer Section
            printama.printTextln("Thank you for your visit!", PA.CENTER);
            printama.printTextln("Please come again", PA.CENTER);
            printama.addNewLine();
            printama.setSmallText();
            printama.printTextln("Powered by Printama Library", PA.CENTER);
            printama.printTextln("Visit: github.com/anggastudio/Printama", PA.CENTER);
            printama.setNormalText();

            printama.addNewLine(3);
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printThreeColumnReceipt() {
        Printama.with(this).connect(printama -> {
            // Header
            printama.setBold();
            printama.printTextln("SALES REPORT", PA.CENTER);
            printama.setNormalText();
            printama.printTextln("Daily Summary", PA.CENTER);
            printama.printDashedLine();

            // Three-column headers using custom formatting
            printama.setBold();
            printama.printTextln(printama.formatThreeColumns("ITEM", "QTY", "TOTAL"), PA.LEFT);
            printama.setNormalText();
            printama.printDashedLine();

            // Three-column data
            printama.printTextln(printama.formatThreeColumns("Coffee", "12", "$36.00"), PA.LEFT);
            printama.printTextln(printama.formatThreeColumns("Tea", "8", "$16.00"), PA.LEFT);
            printama.printTextln(printama.formatThreeColumns("Pastry", "5", "$25.00"), PA.LEFT);
            printama.printTextln(printama.formatThreeColumns("Sandwich", "3", "$21.00"), PA.LEFT);

            printama.printDashedLine();
            printama.setBold();
            printama.printTextln(printama.formatThreeColumns("TOTAL", "28", "$98.00"), PA.LEFT);
            printama.setNormalText();
            printama.addNewLine(2);
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printFourColumnReceipt() {
        Printama.with(this).connect(printama -> {
            // Header
            printama.setBold();
            printama.printTextln("INVENTORY REPORT", PA.CENTER);
            printama.setNormalText();
            printama.printTextln("Stock Status", PA.CENTER);
            printama.printDashedLine();

            // Four-column headers using custom formatting
            printama.setBold();
            printama.printTextln(printama.formatFourColumns("ITEM", "QTY", "MIN", "STATUS"), PA.LEFT);
            printama.setNormalText();
            printama.printDashedLine();

            // Four-column data
            printama.printTextln(printama.formatFourColumns("Coffee", "45", "20", "OK"), PA.LEFT);
            printama.printTextln(printama.formatFourColumns("Tea", "12", "15", "LOW"), PA.LEFT);
            printama.printTextln(printama.formatFourColumns("Sugar", "8", "10", "LOW"), PA.LEFT);
            printama.printTextln(printama.formatFourColumns("Milk", "25", "20", "OK"), PA.LEFT);

            printama.printDashedLine();
            printama.printTextln("Report generated automatically", PA.CENTER);
            printama.addNewLine(2);
            printama.close();
        }, this::showToast);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printExample1() {
        QRCodeWriter writer = new QRCodeWriter();
        Printama.with(this).connect(print -> {
            try {
                print.printTextln("SHOP RECEIPT", PA.CENTER);
                // Use ZXing's QRCodeWriter (already imported) instead of BarcodeEncoder
                Bitmap bitmap200 = bitMatrixToBitmap(
                        writer.encode("MX250700006946093216", BarcodeFormat.QR_CODE, 200, 200)
                );
                print.printImage(bitmap200, PW.HALF_WIDTH, PA.CENTER);
                print.printTextln("MX250700006946093216", PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextln("ORDER", PA.CENTER);
                // Widen the right column so “Rp 25.000,00” fits on 2-inch printers
                print.printTextln(print.formatTwoColumns("FRIED RICE", "Rp 25.000,00", 0.60, 0.40), PA.LEFT);
                print.printTextln(print.formatTwoColumns("CHICKEN NOODLES", "Rp 20.000,00", 0.60, 0.40), PA.LEFT);
                print.printTextln(print.formatTwoColumns("ICED SWEET TEA", "Rp 7.000,00", 0.60, 0.40), PA.LEFT);
                print.printTextln(print.formatTwoColumns("NORMAL TEA", "Rp 5.000,00", 0.60, 0.40), PA.LEFT);
                print.printTextln("", PA.LEFT);
                print.printTextlnBold("--------------------------------", PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextln("SARAN DAN MASUKAN", PA.CENTER);
                print.printTextln("", PA.LEFT);
                Bitmap bitmap160 = bitMatrixToBitmap(
                        writer.encode("http://mysite.com", BarcodeFormat.QR_CODE, 200, 200)
                );
                print.printImage(bitmap160, PW.HALF_WIDTH, PA.CENTER);
                print.printTextln("http://mysite.com", PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextln("HOPE YOU  ENJOY WITH OUR FOOD AND SERVICES", PA.CENTER);
                print.printTextln("WISH YOU ALL THE BEST", PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextln("--------- THANK YOU ---------", PA.CENTER);
                print.printTextln("", PA.LEFT);
                Bitmap bitmap150 = ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.printama_colorful_logo)).getBitmap();
                print.printImage(bitmap150, PW.THREE_QUARTERS_WIDTH, PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextlnBold("--------------------------------", PA.CENTER);
                print.printTextln("", PA.LEFT);
                print.printTextln("", PA.LEFT);
                print.printTextln("", PA.LEFT);
                print.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printExample2() {

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper: convert ZXing BitMatrix → Bitmap for printing
    private Bitmap bitMatrixToBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }


}
