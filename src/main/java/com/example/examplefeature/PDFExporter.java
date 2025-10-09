package com.example.examplefeature;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFExporter {

    /**
     * Exporta a lista de tarefas para um ficheiro PDF.
     *
     * @param tasks    Lista de tarefas
     * @param filePath Caminho do ficheiro PDF a gerar
     */
    public static void exportToPDF(List<String> tasks, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Fonte base
                PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);


                float y = page.getMediaBox().getHeight() - 50;

                // Título
                content.beginText();
                content.setFont(font, 16);
                content.newLineAtOffset(50, y);
                content.showText("Lista de Tarefas");
                content.endText();

                y -= 25;

                // Data
                String data = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                content.beginText();
                content.setFont(font, 12);
                content.newLineAtOffset(50, y);
                content.showText("Data: " + data);
                content.endText();

                y -= 30;

                // Tarefas numeradas
                int numero = 1;
                for (String task : tasks) {
                    content.beginText();
                    content.setFont(font, 12);
                    content.newLineAtOffset(50, y);
                    content.showText(numero + ". " + task);
                    content.endText();
                    y -= 20;
                    numero++;
                }
            }

            // Salva o PDF
            document.save(filePath);
            System.out.println("PDF gerado com sucesso em: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
