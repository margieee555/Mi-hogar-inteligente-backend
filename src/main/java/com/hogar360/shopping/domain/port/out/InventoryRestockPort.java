package com.hogar360.shopping.domain.port.out;

/** Contrato hacia el módulo de inventario: reponer stock al comprar y deshacerlo al desmarcar. */
public interface InventoryRestockPort {

    /** Suma la cantidad al artículo de inventario con ese nombre. Devuelve false si no existe. */
    boolean restock(Long householdId, String itemName, int amount);

    /** Resta la cantidad que se había repuesto (sin bajar de cero). No hace nada si el artículo ya no existe. */
    void undoRestock(Long householdId, String itemName, int amount);
}