package org.example;

public enum InputType {
    RANDOM("random"),
    SORTED("sorted"),
    DUPLICATES("duplicates");

    private final String csvName;

    InputType(String csvName) {
        this.csvName = csvName;
    }

    public String csvName() {
        return csvName;
    }
}
