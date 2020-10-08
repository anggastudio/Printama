package com.anggastudio.sample.mock;

import com.anggastudio.sample.model.PrintBody;
import com.anggastudio.sample.model.PrintFooter;
import com.anggastudio.sample.model.PrintHeader;
import com.anggastudio.sample.model.PrintModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Mock {

    private Mock() {

    }

    public static PrintModel getPrintModelMock() {
        PrintBody printBody = new PrintBody();
        printBody.setDate("17/08/2020");
        printBody.setTime("22:29:52");
        printBody.setInvoice("000034");
        printBody.setQrCode("test qr code");
        printBody.setTotalPayment("Rp125.000");

        Date date = new Date();
        date.setTime(System.currentTimeMillis());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String format = simpleDateFormat.format(date);
        printBody.setTimeStamp(format);

        PrintFooter printFooter = new PrintFooter();
        printFooter.setInitial("INI BUKAN BUKTI PEMBAYARAN SAH");
        printFooter.setPaymentBy("Acquirer ACQ1");
        printFooter.setPowered("*** POWERED BY PRINTAMA ***");
        printFooter.setEnvironment("Development");

        PrintHeader printHeader = new PrintHeader();

        //set print header
        printHeader.setMerchantAddress1("Jalan Mawar No 7");
        printHeader.setMerchantAddress2("Cibinong Bogor");
        printHeader.setMerchantName("Kedai Juragan Bebek");
        printHeader.setMerchantId("008");

        return new PrintModel(printHeader, printBody, printFooter);
    }
}
