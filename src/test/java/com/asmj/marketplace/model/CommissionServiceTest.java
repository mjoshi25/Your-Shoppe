package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.asmj.marketplace.commission.*;
import com.asmj.marketplace.order.MarketplaceOrder;

class CommissionServiceTest {
 @Test void recordsCommissionUsingOrderCompletionWeekAndCapturedRate(){
  CommissionLedgerRepository ledger=mock(CommissionLedgerRepository.class);
  CommissionSettingsRepository settings=mock(CommissionSettingsRepository.class);
  when(settings.findById("commission")).thenReturn(Optional.empty());
  when(ledger.findByOrderId("order-1")).thenReturn(Optional.empty());
  CommissionService service=new CommissionService(ledger,settings);
  service.setRatePercent(new BigDecimal("7.50"));
  MarketplaceOrder order=new MarketplaceOrder();order.setId("order-1");order.setOrderNumber("ASMJ-TEST");order.setVendorUserId("vendor-1");order.setVendorName("Test Vendor");order.setSubtotal(new BigDecimal("1000.00"));
  order.setCompletedAt(Instant.parse("2026-11-10T10:00:00Z"));order.setUpdatedAt(Instant.parse("2026-11-18T10:00:00Z"));
  service.recordCompletedOrder(order);
  ArgumentCaptor<CommissionLedger> captor=ArgumentCaptor.forClass(CommissionLedger.class);
  verify(ledger).save(captor.capture());
  CommissionLedger saved=captor.getValue();
  assertEquals(new BigDecimal("75.00"),saved.getCommissionAmount());
  assertEquals(new BigDecimal("7.50"),saved.getRatePercent());
  LocalDate completionDate=order.getCompletedAt().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
  assertEquals(completionDate.with(java.time.DayOfWeek.MONDAY),saved.getWeekStart());
  assertEquals(saved.getWeekStart().plusDays(6),saved.getWeekEnd());
 }
 @Test void doesNotCreateDuplicateLedgerForSameOrder(){
  CommissionLedgerRepository ledger=mock(CommissionLedgerRepository.class);CommissionSettingsRepository settings=mock(CommissionSettingsRepository.class);
  when(settings.findById("commission")).thenReturn(Optional.empty());when(ledger.findByOrderId("order-2")).thenReturn(Optional.of(new CommissionLedger()));
  CommissionService service=new CommissionService(ledger,settings);MarketplaceOrder order=new MarketplaceOrder();order.setId("order-2");order.setVendorUserId("vendor-1");order.setSubtotal(BigDecimal.TEN);
  service.recordCompletedOrder(order);verify(ledger,never()).save(any(CommissionLedger.class));
 }
 @Test void vendorUplodSubmissionStoresUtrAndProofWithoutMarkingPaid(){
  CommissionLedgerRepository ledger=mock(CommissionLedgerRepository.class);CommissionSettingsRepository settings=mock(CommissionSettingsRepository.class);
  when(settings.findById("commission")).thenReturn(Optional.empty());CommissionLedger entry=new CommissionLedger();entry.setId("ledger-1");entry.setVendorUserId("vendor-1");entry.setStatus("UNPAID");when(ledger.findById("ledger-1")).thenReturn(Optional.of(entry));
  CommissionService service=new CommissionService(ledger,settings);service.submitPayment("ledger-1","vendor-1","UPI","UTR123","https://proof.example/proof.png","proof.png");
  assertEquals("UNPAID",entry.getStatus());assertEquals("UTR123",entry.getUpiReference());assertEquals("https://proof.example/proof.png",entry.getPaymentProofUrl());verify(ledger).save(entry);
 }
 @Test void rejectsCommissionPaymentSubmissionFromDifferentVendor(){
  CommissionLedgerRepository ledger=mock(CommissionLedgerRepository.class);CommissionSettingsRepository settings=mock(CommissionSettingsRepository.class);when(settings.findById("commission")).thenReturn(Optional.empty());CommissionLedger entry=new CommissionLedger();entry.setVendorUserId("owner");when(ledger.findById("ledger-2")).thenReturn(Optional.of(entry));CommissionService service=new CommissionService(ledger,settings);
  assertThrows(IllegalArgumentException.class,()->service.submitPayment("ledger-2","attacker","CASH",null,null,null));verify(ledger,never()).save(any(CommissionLedger.class));
 }

}
