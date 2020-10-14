package share.money.wallet.shared;

public enum TicketStatus {
    TICKET_RESERVED("ticket_reserved"),
    TICKET_APPROVED("ticket_approved");

    private String status;

    TicketStatus(String status) {
        this.status = status;
    }

    public String val() {
        return status;
    }
}
