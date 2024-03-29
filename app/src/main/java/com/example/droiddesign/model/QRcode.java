package com.example.droiddesign.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.UUID;

public class QRcode {
    private String qrId, eventId, type;
    private Bitmap mQrBitmap;

    public QRcode(String eventId, String type) {
        this.eventId = eventId;
        this.type = type;

        if (!"share".equals(type) && !"check_in".equals(type)) {
            throw new IllegalArgumentException("Invalid type: " + type + ". Type must be either 'share' or 'check_in'.");
        }

        this.qrId = UUID.randomUUID().toString();

        // Generate the actual QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrId, BarcodeFormat.QR_CODE, 200, 200);
            BitMatrix trimmedMatrix = getTrimmedMatrix(bitMatrix);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            mQrBitmap = barcodeEncoder.createBitmap(trimmedMatrix);
        } catch (WriterException e) {
            Log.e("QrCodeGenerator", "Error generating QR code", e);
        }
    }

    @NonNull
    private static BitMatrix getTrimmedMatrix(BitMatrix bitMatrix) {
        int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
        int startX = enclosingRectangle[0];
        int startY = enclosingRectangle[1];
        int width = enclosingRectangle[2];
        int height = enclosingRectangle[3];

        BitMatrix trimmedMatrix = new BitMatrix(width, height);
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                if (bitMatrix.get(i, j)) {
                    trimmedMatrix.set(i - startX, j - startY);
                }
            }
        }
        return trimmedMatrix;
    }

    public Bitmap getmQrBitmap() {
        return this.mQrBitmap;
    }

    public void setmQrBitmap(Bitmap mQrBitmap) {
        this.mQrBitmap = mQrBitmap;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

        if (!"share".equals(type) && !"check_in".equals(type)) {
            throw new IllegalArgumentException("Invalid type: " + type + ". Type must be either 'share' or 'check_in'.");
        }
    }
}