package com.hogar360.shopping.domain.port.out;

import com.hogar360.shopping.domain.model.ShoppingItem;

import java.util.List;
import java.util.Optional;

public interface ShoppingRepositoryPort {
    ShoppingItem save(ShoppingItem item);

    /** Busca por id DENTRO del hogar: un producto de otro hogar se ve como inexistente. */
    Optional<ShoppingItem> findByIdAndHouseholdId(Long id, Long householdId);

    /** Pendientes primero y luego los comprados; dentro de cada grupo, por nombre. */
    List<ShoppingItem> findByHousehold(Long householdId);

    void deleteById(Long id);

    /** Borra los productos ya comprados del hogar y devuelve cuántos eran. */
    int deleteBoughtByHousehold(Long householdId);
}
