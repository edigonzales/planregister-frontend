package ch.so.arp.planregister;

public record Predicate(
        String databaseColumn,
        String value,
        int argType,
        String operator
        ) {}
