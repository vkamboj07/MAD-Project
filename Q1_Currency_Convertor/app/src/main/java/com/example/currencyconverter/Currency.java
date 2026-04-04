package com.example.currencyconverter;

public enum Currency {
    INR("INR", "Indian Rupee", "🇮🇳"),
    USD("USD", "US Dollar", "🇺🇸"),
    JPY("JPY", "Japanese Yen", "🇯🇵"),
    EUR("EUR", "Euro", "🇪🇺");

    public final String code;
    public final String name;
    public final String flag;

    Currency(String code, String name, String flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return flag + "  " + code + " – " + name;
    }
}
