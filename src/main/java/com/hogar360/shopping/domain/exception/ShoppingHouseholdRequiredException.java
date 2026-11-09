package com.hogar360.shopping.domain.exception;

public class ShoppingHouseholdRequiredException extends RuntimeException {

    public ShoppingHouseholdRequiredException() {
        super("Debes configurar tu hogar antes de usar la lista de compras");
    }
}
