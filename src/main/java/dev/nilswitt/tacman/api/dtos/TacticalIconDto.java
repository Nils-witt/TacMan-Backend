package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.TacticalIcon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public TacticalIconDto() {}

    public TacticalIconDto(TacticalIcon icon) {
        try {
            this.name = icon.getName();
            this.organisationName = icon.getOrganisationName();
            this.typ = icon.getTyp();
            this.text = icon.getText();
            this.einheit = icon.getEinheit().getId();
            this.fachaufgabe = icon.getFachaufgabe().getId();
            this.grundzeichen = icon.getGrundzeichen().getId();
            this.organisation = icon.getOrganisation().getId();
            this.symbol = icon.getSymbol().getId();
            this.funktion = icon.getFunktion().getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
