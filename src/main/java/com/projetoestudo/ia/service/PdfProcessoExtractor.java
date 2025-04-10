package com.projetoestudo.ia.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfProcessoExtractor {
    public String extrairTextoOuOCR(MultipartFile file, String tessdataPath) throws Exception {
        File pdf = File.createTempFile("temp", ".pdf");
        file.transferTo(pdf);

        try (PDDocument doc = PDDocument.load(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            PDFRenderer renderer = new PDFRenderer(doc);

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String texto = stripper.getText(doc);

                String processo = buscarNumeroProcesso(texto);
                if (processo != null) return processo;

                // OCR
                BufferedImage img = renderer.renderImageWithDPI(i, 300);
                File imgTemp = File.createTempFile("img", ".png");
                ImageIO.write(img, "png", imgTemp);

                ITesseract ocr = new Tesseract();
                ocr.setDatapath(tessdataPath);
                ocr.setLanguage("por");

                String ocrTexto = ocr.doOCR(imgTemp);
                processo = buscarNumeroProcesso(ocrTexto);
                if (processo != null) return processo;
            }
        }

        return null;
    }

    private String buscarNumeroProcesso(String texto) {
        Pattern pattern = Pattern.compile("(\\d{7}-\\d{2}\\.\\d{4}\\.\\d\\.\\d{2}\\.\\d{4})");
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group(1) : null;
    }
}
