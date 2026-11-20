package ua.kpi.comsys.dop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderProcessorTest {

    private OrderProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrderProcessor();
    }

    @Test
    void shouldProcessPendingStatus() {
        OrderStatus status = new Pending();
        String expected = """
                Order Status: PENDING
                Details: Order is currently pending processing.""";

        assertEquals(expected, processor.process(status));
    }

    @Test
    void shouldProcessPaidStatus() {
        OrderStatus status = new Paid("PAY-12345", new BigDecimal("199.99"));
        String expected = """
                Order Status: PAID
                Details: Payment ID: PAY-12345, Amount: $199.99""";

        assertEquals(expected, processor.process(status));
    }

    @Test
    void shouldProcessShippedStatus() {
        OrderStatus status = new Shipped("UA987654321", LocalDate.of(2026, 11, 20));
        String expected = """
                Order Status: SHIPPED
                Details: Tracking Code: UA987654321, Dispatch Date: 2026-11-20""";

        assertEquals(expected, processor.process(status));
    }

    @Test
    void shouldProcessCancelledStatus() {
        OrderStatus status = new Cancelled("Out of stock");
        String expected = """
                Order Status: CANCELLED
                Details: Reason: Out of stock""";

        assertEquals(expected, processor.process(status));
    }

    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.process(null)
        );
        assertEquals("Status cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void shouldThrowExceptionWhenPaidIdIsInvalid(String invalidId) {
        BigDecimal validAmount = BigDecimal.TEN;
        assertThrows(IllegalArgumentException.class, () -> new Paid(invalidId, validAmount));
    }

    @Test
    void shouldThrowExceptionWhenPaidAmountIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Paid("PAY-1", null));
        assertThrows(IllegalArgumentException.class, () -> new Paid("PAY-1", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new Paid("PAY-1", new BigDecimal("-50.00")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void shouldThrowExceptionWhenTrackingCodeIsInvalid(String invalidCode) {
        LocalDate validDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () -> new Shipped(invalidCode, validDate));
    }

    @Test
    void shouldThrowExceptionWhenDispatchDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Shipped("TRACK-1", null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n"})
    void shouldThrowExceptionWhenCancelReasonIsInvalid(String invalidReason) {
        assertThrows(IllegalArgumentException.class, () -> new Cancelled(invalidReason));
    }
}