package com.icoderman.shopify;

/**
 * Enum that represents WooCommerce API versions
 */
public enum ApiVersionType {
    V1("2024-01"),
    V2("v2"),
    V202010("");

    private String value;

    ApiVersionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
