package com.hogar360.shopping.application.service;

import com.hogar360.shopping.application.dto.CreateShoppingItemCommand;
import com.hogar360.shopping.domain.exception.ShoppingItemNotFoundException;
import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.domain.port.in.ManageShoppingUseCase;
import com.hogar360.shopping.domain.port.out.InventoryRestockPort;
import com.hogar360.shopping.domain.port.out.ShoppingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShoppingService implements ManageShoppingUseCase {

    private final ShoppingRepositoryPort repository;
    private final ShoppingHouseholdResolver householdResolver;
    private final InventoryRestockPort restockPort;

    public ShoppingService(ShoppingRepositoryPort repository, ShoppingHouseholdResolver householdResolver,
                           InventoryRestockPort restockPort) {
        this.repository = repository;
        this.householdResolver = householdResolver;
        this.restockPort = restockPort;
    }

    @Override
    public ShoppingItem create(CreateShoppingItemCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());

        ShoppingItem item = ShoppingItem.createNew(householdId, command.name(), command.category(),
                command.quantity());

        return repository.save(item);
    }

    @Override
    public List<ShoppingItem> list(String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return repository.findByHousehold(householdId);
    }

    @Override
    public ShoppingItem changeQuantity(Long itemId, String ownerEmail, int quantity) {
        return repository.save(find(itemId, ownerEmail).withQuantity(quantity));
    }

    /**
     * Marcar como comprado repone el inventario (si existe un artículo con ese nombre);
     * desmarcar deshace exactamente lo que se repuso. Todo en una sola transacción.
     */
    @Override
    @Transactional
    public ShoppingItem setBought(Long itemId, String ownerEmail, boolean bought) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        ShoppingItem existing = repository.findByIdAndHouseholdId(itemId, householdId)
                .orElseThrow(() -> new ShoppingItemNotFoundException(itemId));

        if (existing.isBought() == bought) {
            return existing; // ya estaba así: no se repite la reposición
        }

        ShoppingItem updated = existing.withBought(bought);
        if (bought) {
            if (restockPort.restock(householdId, existing.getName(), existing.getQuantity())) {
                updated = updated.withRestockedQuantity(existing.getQuantity());
            }
        } else if (existing.getRestockedQuantity() != null) {
            restockPort.undoRestock(householdId, existing.getName(), existing.getRestockedQuantity());
        }
        return repository.save(updated);
    }

    @Override
    public void delete(Long itemId, String ownerEmail) {
        repository.deleteById(find(itemId, ownerEmail).getId());
    }

    @Override
    @Transactional
    public int clearBought(String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return repository.deleteBoughtByHousehold(householdId);
    }

    private ShoppingItem find(Long itemId, String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return repository.findByIdAndHouseholdId(itemId, householdId)
                .orElseThrow(() -> new ShoppingItemNotFoundException(itemId));
    }
}