package dev.nilswitt.tacman.api.dtos;

import lombok.Data;

@Data
public class TacticalIconDto {

    private String grundzeichen;

    private String organisation;

    private String fachaufgabe;

    private String einheit;

    private String verwaltungsstufe;
    private String funktion;

    private String symbol;
    private String text;
    private String typ;
    private String name;
    private String organisationName;
}
