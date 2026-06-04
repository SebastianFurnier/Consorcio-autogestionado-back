package com.tpgdb.Consorcio.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.tpgdb.Consorcio.Dto.Report.*;
import java.time.LocalDate;
import java.util.List;

import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Repository.PaymentRepository;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import com.tpgdb.Consorcio.Repository.ConsorcioRepository;

import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.Rectangle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;
    private final ExpenseRepository expenseRepository;
    private final PartnerRepository partnerRepository;
    private final ConsorcioRepository consorcioRepository;

    public MonthlySummaryDto getMonthlySummary(Long consorcioId, String period) {
        LocalDate month = LocalDate.parse(period);
        LocalDate startOfMonth = month.withDayOfMonth(1);
        LocalDate endOfMonth = month.withDayOfMonth(month.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByConsorcioIdAndDateBetween(consorcioId, startOfMonth, endOfMonth);
        List<Payment> payments = paymentRepository.findByConsorcioIdAndPeriod(consorcioId, startOfMonth);
        List<Debt> debts = debtRepository.findByConsorcio_id(consorcioId);

        float totalExpenses = (float) expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        float totalPayments = (float) payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        float pendingDebt = (float) debts.stream()
                .filter(d -> !d.isPaid())
                .mapToDouble(Debt::getAmount)
                .sum();

        int totalPartners =
                partnerRepository.countPartnerByActiveTrueAndConsorcio_id(consorcioId);

        int partnersInDebt =
                debtRepository.countDistinctPartnersWithUnpaidDebts(consorcioId);

        float balance = totalPayments - totalExpenses;

        return new MonthlySummaryDto(
                period,
                totalExpenses,
                totalPayments,
                pendingDebt,
                totalPartners,
                partnersInDebt,
                balance
        );
    }

    public byte[] generateMonthlySummaryPdf(Long consorcioId, String period) {
        MonthlySummaryDto summary = getMonthlySummary(consorcioId, period);
        Consorcio consorcio = consorcioRepository.findById(consorcioId)
                .orElseThrow(() -> new RuntimeException("Consorcio no encontrado"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Reporte Mensual - " + consorcio.getNombre(), titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        
        // Período
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("Período: " + summary.getPeriod(), normalFont));
        document.add(new Paragraph(" "));
        
        // Tabla de resumen
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        // Encabezados
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell cell;
        
        addTableRow(table, "Total Gastos", formatMoney(summary.getTotalExpenses()), headerFont, normalFont);
        addTableRow(table, "Total Pagos", formatMoney(summary.getTotalPayments()), headerFont, normalFont);
        addTableRow(table, "Deuda Pendiente", formatMoney(summary.getPendingDebt()), headerFont, normalFont);
        addTableRow(table, "Balance", formatMoney(summary.getBalance()), headerFont, normalFont);
        addTableRow(table, "Total Socios", String.valueOf(summary.getTotalPartners()), headerFont, normalFont);
        addTableRow(table, "Socios en Deuda", String.valueOf(summary.getPartnersInDebt()), headerFont, normalFont);
        
        document.add(table);
        
        document.close();
        
        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String label, String value, Font headerFont, Font normalFont) {
        PdfPCell cell1 = new PdfPCell(new Paragraph(label, headerFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph(value, normalFont));
        cell1.setBorder(Rectangle.BOX);
        cell2.setBorder(Rectangle.BOX);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    private String formatMoney(float amount) {
        return String.format("$ %,.2f", amount);
    }


}