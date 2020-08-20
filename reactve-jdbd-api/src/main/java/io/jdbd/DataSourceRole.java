package io.jdbd;

public enum DataSourceRole {

    PRIMARY,
    SECONDARY,
    TIMEOUT_SECONDARY;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
