package ua.kpi.comsys.dop;

public class OrderProcessor {

    public String process(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        return switch (status) {
            case Pending() -> """
                    Order Status: PENDING
                    Details: Order is currently pending processing.""";

            case Paid(var paymentId, var amount) -> """
                    Order Status: PAID
                    Details: Payment ID: %s, Amount: $%s""".formatted(paymentId, amount);

            case Shipped(var trackingCode, var dispatchDate) -> """
                    Order Status: SHIPPED
                    Details: Tracking Code: %s, Dispatch Date: %s""".formatted(trackingCode, dispatchDate);

            case Cancelled(var reason) -> """
                    Order Status: CANCELLED
                    Details: Reason: %s""".formatted(reason);
        };
    }
}