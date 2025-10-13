package com.example.minimal;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceRepository repo;

  public InvoiceController(InvoiceRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Invoice> list() {
    return repo.findAll();
  }

  @PostMapping
  public Invoice create(@RequestBody Invoice invoice) {
    return repo.save(invoice);
  }
}