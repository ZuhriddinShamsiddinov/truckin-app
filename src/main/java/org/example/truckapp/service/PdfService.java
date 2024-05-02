package org.example.truckapp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.extern.slf4j.Slf4j;
import org.example.truckapp.dto.TruckDto;


@Slf4j
public class PdfService {

    public static byte[] generatePdf(List<TruckDto> truckDtoList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);


        Paragraph title = new Paragraph("Truck Information");
        document.add(title);

        Table table = new Table(7);
        table.setWidth(100);

        table.addCell(new Paragraph("ID"));
        table.addCell(new Paragraph("Number"));
        table.addCell(new Paragraph("Current Position"));
        table.addCell(new Paragraph("Last Total Distance"));
        table.addCell(new Paragraph("Total Distance"));
        table.addCell(new Paragraph("Last Sent Time"));
        table.addCell(new Paragraph("Last Average Speed"));

        for (TruckDto truckDto : truckDtoList) {
            table.addCell(new Paragraph(String.valueOf(truckDto.getId())));
            table.addCell(new Paragraph(truckDto.getNumber()));
            table.addCell(new Paragraph(truckDto.getCurrentPosition()));
            table.addCell(new Paragraph(truckDto.getLastTotalDistance() + " km"));
            table.addCell(new Paragraph(truckDto.getTotalDistance() + " km"));
            table.addCell(new Paragraph(truckDto.getLastSentTime()));
            table.addCell(new Paragraph(truckDto.getLastAverageSpeed() + " km/h"));
        }

        document.add(table);

        document.close();
        pdfDocument.close();
        writer.close();

        return byteArrayOutputStream.toByteArray();
    }


}