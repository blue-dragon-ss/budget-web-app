package com.example.minimal.controller;

import org.springframework.web.bind.annotation.*;

import com.example.minimal.model.InvoiceModel;
import com.example.minimal.repository.InvoiceRepository;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceRepository repo;

  public InvoiceController(InvoiceRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<InvoiceModel> list() {
    return repo.findAll();
  }

  @PostMapping
  public InvoiceModel create(@RequestBody InvoiceModel invoice) {
    return repo.save(invoice);
  }
}