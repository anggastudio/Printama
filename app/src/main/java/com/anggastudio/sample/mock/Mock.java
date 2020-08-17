package com.anggastudio.sample.mock;

import com.anggastudio.sample.model.PrintBody;
import com.anggastudio.sample.model.PrintFooter;
import com.anggastudio.sample.model.PrintHeader;
import com.anggastudio.sample.model.PrintModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Mock {

    public static PrintModel getPrintModelMock() {
        PrintBody printBody = new PrintBody();
        printBody.setDate("17/08/2020");
        printBody.setTime("22:29:52");
        printBody.setInvoice("000034");
        printBody.setQrCode("test qr code");
        printBody.setTotalPayment("Rp125.000");

        Date date = new Date();
        date.setTime(System.currentTimeMillis());

        printBody.setTimeStamp(new SimpleDateFormat("HH:mm:ss").format(date));

        PrintFooter printFooter = new PrintFooter();
        printFooter.setInitial("INI BUKAN BUKTI PEMBAYARAN SAH");
        printFooter.setPaymentBy("Acquirer ACQ1");
        printFooter.setPowered("*** POWERED BY PRINTAMA ***");
        printFooter.setEnvironment("Development");

        PrintHeader printHeader = new PrintHeader();

        //set print heeader
        printHeader.setMerchantAddress1("Jalan Mawar No 7");
        printHeader.setMerchantAddress2("Cibinong Bogor");
        printHeader.setMerchantName("Kedai Juragan Bebek");
        printHeader.setMerchantId("008");

        return new PrintModel(printHeader, printBody, printFooter);
    }
}
