package com.example.examplefeature;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.flow.component.html.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class QrCodeGenerator {


    public static Image generateQrCode(String text, int size) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            byte[] pngData = pngOutputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pngData);
            String dataUri = "data:image/png;base64," + base64;

            Image qrImage = new Image(dataUri, "QR Code");
            qrImage.setWidth(size + "px");
            qrImage.setHeight(size + "px");

            return qrImage;

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
}
