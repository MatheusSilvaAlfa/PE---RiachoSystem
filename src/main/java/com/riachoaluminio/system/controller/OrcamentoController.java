package com.riachoaluminio.system.controller;

import com.riachoaluminio.system.entity.Orcamento;
import com.riachoaluminio.system.service.OrcamentoService;
import com.riachoaluminio.system.service.PdfOrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {
    private final OrcamentoService orcamentoService;
    private final PdfOrcamentoService pdfOrcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService, PdfOrcamentoService pdfOrcamentoService) {
        this.orcamentoService = orcamentoService;
        this.pdfOrcamentoService = pdfOrcamentoService;
    }

    @GetMapping
    public List<Orcamento> listarTodos() {
        return orcamentoService.listarTodos();
    }

    @PostMapping
    public Orcamento salvar(@Valid @RequestBody Orcamento orcamento) {
        return orcamentoService.salvar(orcamento);
    }

    @PutMapping("/{id}")
    public Orcamento atualizar(@PathVariable Long id, @Valid @RequestBody Orcamento orcamento) {
        return orcamentoService.atualizar(id, orcamento);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        orcamentoService.excluir(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdfOrcamento(@PathVariable Long id) throws Exception {
        Orcamento orcamento = orcamentoService.findById(id);
        byte[] pdfBytes = pdfOrcamentoService.gerarPdf(orcamento);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("orcamento.pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
