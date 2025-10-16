package com.example.minimal.controller;

import org.springframework.web.bind.annotation.*;

import com.example.minimal.model.InvoiceModel;
import com.example.minimal.repository.InvoiceRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Invoices", description = "請求API")
@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceRepository repo;

  public InvoiceController(InvoiceRepository repo) {
    this.repo = repo;
  }

  @Operation(summary = "請求一覧取得")
  @GetMapping
  public List<InvoiceModel> list() {
    return repo.findAll();
  }

  @Operation(summary = "請求登録")
  @PostMapping
  public InvoiceModel create(@RequestBody InvoiceModel invoice) {
    return repo.save(invoice);
  }
}