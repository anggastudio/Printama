package com.anggastudio.sample;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintamaPrintService extends PrintService {

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        return new PrinterDiscoverySession() {
            @Override
            public void onStartPrinterDiscovery(@NonNull List<PrinterId> priorityList) {
                Log.d("myprinter", "PrinterDiscoverySession#onStartPrinterDiscovery(priorityList: " + priorityList + ") called");

                List<PrinterInfo> printers = new ArrayList<>();

                PrinterId printerId = generatePrinterId("1001");
                boolean isExist = false;
                if (!printers.isEmpty()) {
                    for (PrinterInfo printer : printers) {
                        isExist = printer.getId().equals(printerId.getLocalId());
                    }
                }

                if (isExist) return;

                PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, "Printama", PrinterInfo.STATUS_IDLE);
                builder.setDescription("Print with Printama App");

                PrinterCapabilitiesInfo.Builder capBuilder = new PrinterCapabilitiesInfo.Builder(printerId);

                capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_C6, true);
                capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_C7, false);
                capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_C8, false);

                capBuilder.addResolution(new PrintAttributes.Resolution("resolutionId", "default resolution", 203, 203), true);

                capBuilder.setColorModes(
                        PrintAttributes.COLOR_MODE_MONOCHROME,
                        PrintAttributes.COLOR_MODE_MONOCHROME
                );

                builder.setCapabilities(capBuilder.build());
                printers.add(builder.build());
                addPrinters(printers);
            }

            @Override
            public void onStopPrinterDiscovery() {
                Log.d("myprinter", "MyPrintService#onStopPrinterDiscovery() called");
            }

            @Override
            public void onValidatePrinters(@NonNull List<PrinterId> printerIds) {
                Log.d("myprinter", "MyPrintService#onValidatePrinters(printerIds: " + printerIds + ") called");
            }

            @Override
            public void onStartPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("myprinter", "MyPrintService#onStartPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("myprinter", "MyPrintService#onStopPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onDestroy() {
                Log.d("myprinter", "MyPrintService#onDestroy() called");
            }
        };
    }

    @Override
    protected void onRequestCancelPrintJob(android.printservice.PrintJob printJob) {
        Log.d("myprinter", "canceled: " + (printJob != null ? printJob.getId() : "null"));

        if (printJob != null) {
            printJob.cancel();
        }
    }

    @Override
    protected void onPrintJobQueued(android.printservice.PrintJob printJob) {
        try {
            PrintDocument printDocument = printJob.getDocument();
            if (printDocument != null) {
                FileInputStream inputStream = new FileInputStream(printDocument.getData().getFileDescriptor());

                // Process the print data as needed
                // Example: Read the content and send it to the printer
                // Example: Convert the content to a printable format
                // Example: Perform any necessary data processing or formatting
                // Example: Send the data to a thermal printer

                showToast("print success");

                inputStream.close();
            }

            printJob.complete();
        } catch (IOException e) {
            Log.e("PrintService", "Error processing print job", e);

            showToast("Error processing print job: " + e.getMessage());

            printJob.cancel();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
