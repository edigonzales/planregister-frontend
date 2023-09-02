package ch.so.arp.planregister;

import java.net.URI;
import java.time.LocalDate;

public record Dokument(
        String planungsinstrument,
        String bezeichnung,
        String planungsbehoerde,
        String gemeinde,
        LocalDate rechtskraftAb,
        String rechtsstatus,
        URI dokumentUrl,
        LocalDate rrbDatum,
        String rrbNr,
        URI rrbUrl,
        URI sonderbauvorschriftenUrl,
        URI karteUrl,
        String zustaendigesAmt,
        boolean aktuelleOrtsplanung,
        String offizielleNr) {}
