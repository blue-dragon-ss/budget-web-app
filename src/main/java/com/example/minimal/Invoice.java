package com.example.minimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tenant_name", nullable = false)
  private String tenantName;

  @Column(name = "amount_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal amountTotal;

  @Column(name = "bill_month", nullable = false)
  private LocalDate billMonth;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  // getter / setter（Lombokを使うなら @Data 等で省略可）
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getTenantName() { return tenantName; }
  public void setTenantName(String tenantName) { this.tenantName = tenantName; }
  public BigDecimal getAmountTotal() { return amountTotal; }
  public void setAmountTotal(BigDecimal amountTotal) { this.amountTotal = amountTotal; }
  public LocalDate getBillMonth() { return billMonth; }
  public void setBillMonth(LocalDate billMonth) { this.billMonth = billMonth; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}