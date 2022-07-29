package de.bravemc.supportchat.utils;

public enum TicketStatus {
    OPEN("geöffnet"),
    IN_PROGRESS("in Bearbeitung"),
    CLOSED("geschlossen"),
    DELETED("geschlossen");

    private String status;

    public String getStatus(){
        return status;
    }

    TicketStatus(final String status) {
        this.status = status;
    }
}
