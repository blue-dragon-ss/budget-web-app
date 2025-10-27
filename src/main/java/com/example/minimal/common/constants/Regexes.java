package com.example.minimal.common.constants;

public final class Regexes {
    private Regexes() {}

    public static final String EMAIL = "^(|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,})$";
    public static final String ULID  = "^[0-9A-HJKMNP-TV-Z]{26}$";
}
