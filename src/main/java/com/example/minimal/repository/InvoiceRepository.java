package com.example.minimal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minimal.model.InvoiceModel;

public interface InvoiceRepository extends JpaRepository<InvoiceModel, Long> {
}