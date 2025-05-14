package com.riachoaluminio.system.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.riachoaluminio.system.entity.Orcamento;
import com.riachoaluminio.system.entity.ItemOrcamento;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PdfOrcamentoService {

    public byte[] gerarPdf(Orcamento orcamento) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Document A4 com margens (36pt = 0,5in aproximadamente)
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            float desiredWidth = 250f;
            float proportion = logo.getWidth() / logo.getHeight();
            float desiredHeight = desiredWidth / proportion;

            logo.scaleAbsolute(desiredWidth, desiredHeight);
            logo.setAlignment(Element.ALIGN_CENTER);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell cellLogo = new PdfPCell(logo, false);
            cellLogo.setBorder(PdfPCell.NO_BORDER);
            cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cellLogo);

            document.add(headerTable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Cabeçalho ---
        Font fonteCabecalho = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

        Font fonteSubtitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Paragraph subtitulo = new Paragraph("Orçamento para fornecimento e instalação de esquadrias (alumínio e vidro)", fonteSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        // Espaço
        document.add(new Paragraph(" "));

        // --- Dados do Cliente, Data e Local ---
        Font fonteDados = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Paragraph dadosOrcamento = new Paragraph();
        dadosOrcamento.setFont(fonteDados);
        // Nome do cliente
        dadosOrcamento.add(new Phrase("Cliente: " + orcamento.getCliente().getNome() + "\n"));

        // Data formatada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        dadosOrcamento.add(new Phrase("Data: " + orcamento.getDataCriacao().format(formatter) + "\n"));

        // Local fixo
        dadosOrcamento.add(new Phrase("Local: Brasília\n"));
        document.add(dadosOrcamento);

        // Espaço
        document.add(new Paragraph(" "));

        // --- Tabela de Itens ---
        // Criar uma tabela com 7 colunas: Modelo, Material, Largura, Altura, Qtd, Valor e Total
        PdfPTable tabelaItens = new PdfPTable(7);
        tabelaItens.setWidthPercentage(100);
        tabelaItens.setSpacingBefore(10f);
        tabelaItens.setSpacingAfter(10f);

        // Cabeçalho com fundo cinza
        Font fonteCabTabela = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        addTableHeader(tabelaItens, "Modelo", fonteCabTabela);
        addTableHeader(tabelaItens, "Material", fonteCabTabela);
        addTableHeader(tabelaItens, "Largura", fonteCabTabela);
        addTableHeader(tabelaItens, "Altura", fonteCabTabela);
        addTableHeader(tabelaItens, "Qtd", fonteCabTabela);
        addTableHeader(tabelaItens, "Valor", fonteCabTabela);
        addTableHeader(tabelaItens, "Total", fonteCabTabela);

        Font fonteCelula = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        List<ItemOrcamento> itens = orcamento.getItens();
        for (ItemOrcamento item : itens) {
            tabelaItens.addCell(criarCelula(item.getModelo(), fonteCelula));
            tabelaItens.addCell(criarCelula(item.getMaterial(), fonteCelula));

            tabelaItens.addCell(criarCelula(String.valueOf(item.getLargura()), fonteCelula));
            tabelaItens.addCell(criarCelula(String.valueOf(item.getAltura()), fonteCelula));

            tabelaItens.addCell(criarCelula(String.valueOf(item.getQuantidade()), fonteCelula));

            tabelaItens.addCell(criarCelula("R$ " + df.format(item.getValorUnitario()), fonteCelula));

            tabelaItens.addCell(criarCelula("R$ " + df.format(item.getValorUnitario().multiply(
                    BigDecimal.valueOf(item.getQuantidade()))), fonteCelula));
        }
        document.add(tabelaItens);

        // --- Dados Adicionais ---
        Paragraph infoAdicional = new Paragraph();
        infoAdicional.setFont(fonteDados);
        infoAdicional.add(new Phrase("Prazo de entrega: " + orcamento.getPrazoEntrega() + " dias úteis\n"));
        infoAdicional.add(new Phrase("Vendedor: " + orcamento.getVendedor() + "\n"));
        infoAdicional.add(new Phrase("Forma de Pagamento: " + orcamento.getFormaPagamento() + "\n"));
        infoAdicional.add(new Phrase("Valor Total: R$ " + df.format(orcamento.getValorTotal()) + "\n"));
        document.add(infoAdicional);

        // --- Valor Total em Destaque ---
        Font fonteTotalDestaque = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Paragraph totalOrcamento = new Paragraph("TOTAL: R$ " + df.format(orcamento.getValorTotal()), fonteTotalDestaque);
        totalOrcamento.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalOrcamento);

        // Espaço
        document.add(new Paragraph(" "));

        // --- Rodapé/Observações ---
        Font fonteRodape = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        Paragraph rodape = new Paragraph("Orçamento válido por 05 dias", fonteRodape);
        rodape.setAlignment(Element.ALIGN_CENTER);
        document.add(rodape);

        document.close();

        return outputStream.toByteArray();
    }

    // Cria uma célula com o texto e fonte fornecidos, centralizada e com padding.
    private PdfPCell criarCelula(String texto, Font fonte) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, fonte));
        celula.setHorizontalAlignment(Element.ALIGN_CENTER);
        celula.setPadding(5f);
        return celula;
    }

    // Adiciona uma célula de cabeçalho à tabela com fundo cinza.
    private void addTableHeader(PdfPTable tabela, String headerTitle, Font fonte) {
        PdfPCell headerCell = new PdfPCell(new Phrase(headerTitle, fonte));
        headerCell.setBackgroundColor(BaseColor.GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(5f);
        tabela.addCell(headerCell);
    }
}
