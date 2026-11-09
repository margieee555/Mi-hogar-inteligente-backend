package com.hogar360.shopping.domain.port.in;

import com.hogar360.shopping.application.dto.CreateShoppingItemCommand;
import com.hogar360.shopping.domain.model.ShoppingItem;

import java.util.List;

public interface ManageShoppingUseCase {
    ShoppingItem create(CreateShoppingItemCommand command);

    List<ShoppingItem> list(String ownerEmail);

    ShoppingItem changeQuantity(Long itemId, String ownerEmail, int quantity);

    ShoppingItem setBought(Long itemId, String ownerEmail, boolean bought);

    void delete(Long itemId, String ownerEmail);

    /** Elimina los productos ya comprados y devuelve cuántos se quitaron. */
    int clearBought(String ownerEmail);
}