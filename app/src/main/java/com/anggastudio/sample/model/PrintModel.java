package com.anggastudio.sample.model;

public class PrintModel {
    PrintHeader printHeader;
    PrintBody printBody;
    PrintFooter printFooter;
    BitMapModel bitMapModel;

    public PrintModel() {
    }

    public PrintModel(PrintHeader printHeader, PrintBody printBody, PrintFooter printFooter) {
        this.printHeader = printHeader;
        this.printBody = printBody;
        this.printFooter = printFooter;
    }

    public PrintHeader getPrintHeader() {
        return printHeader;
    }

    public void setPrintHeader(PrintHeader printHeader) {
        this.printHeader = printHeader;
    }

    public PrintBody getPrintBody() {
        return printBody;
    }

    public void setPrintBody(PrintBody printBody) {
        this.printBody = printBody;
    }

    public PrintFooter getPrintFooter() {
        return printFooter;
    }

    public void setPrintFooter(PrintFooter printFooter) {
        this.printFooter = printFooter;
    }

    public BitMapModel getBitMapModel() {
        return bitMapModel;
    }

    public void setBitMapModel(BitMapModel bitMapModel) {
        this.bitMapModel = bitMapModel;
    }
}
