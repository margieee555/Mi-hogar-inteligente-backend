package com.hogar360.shopping.application.service;

import com.hogar360.shopping.application.dto.CreateShoppingItemCommand;
import com.hogar360.shopping.domain.exception.ShoppingHouseholdRequiredException;
import com.hogar360.shopping.domain.exception.ShoppingItemNotFoundException;
import com.hogar360.shopping.domain.model.ShoppingCategory;
import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.domain.port.out.InventoryRestockPort;
import com.hogar360.shopping.domain.port.out.ShoppingRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShoppingServiceTest {

    private static final String EMAIL = "ana@x.com";

    private final ShoppingRepositoryPort repository = mock(ShoppingRepositoryPort.class);
    private final ShoppingHouseholdResolver resolver = mock(ShoppingHouseholdResolver.class);
    private final InventoryRestockPort restockPort = mock(InventoryRestockPort.class);
    private final ShoppingService service = new ShoppingService(repository, resolver, restockPort);

    private ShoppingItem existing() {
        return new ShoppingItem(7L, 5L, "Pan", ShoppingCategory.FOOD, 2, false, null, LocalDateTime.now());
    }

    private ShoppingItem alreadyBoughtAndRestocked() {
        return new ShoppingItem(7L, 5L, "Pan", ShoppingCategory.FOOD, 2, true, LocalDateTime.now(),
                LocalDateTime.now(), 2);
    }

    @Test
    void create_guardaElProductoEnElHogarDelUsuario() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.save(any(ShoppingItem.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateShoppingItemCommand(EMAIL, " Manzanas ", ShoppingCategory.FOOD, 3));

        ArgumentCaptor<ShoppingItem> captor = ArgumentCaptor.forClass(ShoppingItem.class);
        verify(repository).save(captor.capture());
        ShoppingItem saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getHouseholdId()).isEqualTo(5L);
        assertThat(saved.getName()).isEqualTo("Manzanas");
        assertThat(saved.getQuantity()).isEqualTo(3);
    }

    @Test
    void create_noGuardaSiElUsuarioNoTieneHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenThrow(new ShoppingHouseholdRequiredException());

        assertThatThrownBy(() -> service.create(
                new CreateShoppingItemCommand(EMAIL, "Pan", ShoppingCategory.FOOD, 1)))
                .isInstanceOf(ShoppingHouseholdRequiredException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void list_devuelveLosProductosDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByHousehold(5L)).thenReturn(List.of(existing()));

        assertThat(service.list(EMAIL)).hasSize(1);
    }

    @Test
    void changeQuantity_guardaLaNuevaCantidad() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(repository.save(any(ShoppingItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingItem result = service.changeQuantity(7L, EMAIL, 6);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getQuantity()).isEqualTo(6);
    }

    @Test
    void setBought_marcaComprado_yReponeElInventario() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(restockPort.restock(5L, "Pan", 2)).thenReturn(true);
        when(repository.save(any(ShoppingItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingItem result = service.setBought(7L, EMAIL, true);

        assertThat(result.isBought()).isTrue();
        assertThat(result.getBoughtAt()).isNotNull();
        assertThat(result.getRestockedQuantity()).isEqualTo(2);
        verify(restockPort).restock(5L, "Pan", 2);
    }

    @Test
    void setBought_sinArticuloEnInventario_noGuardaReposicion() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(restockPort.restock(5L, "Pan", 2)).thenReturn(false);
        when(repository.save(any(ShoppingItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingItem result = service.setBought(7L, EMAIL, true);

        assertThat(result.isBought()).isTrue();
        assertThat(result.getRestockedQuantity()).isNull();
    }

    @Test
    void setBought_desmarcar_deshaceLaReposicion() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(alreadyBoughtAndRestocked()));
        when(repository.save(any(ShoppingItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingItem result = service.setBought(7L, EMAIL, false);

        assertThat(result.isBought()).isFalse();
        assertThat(result.getBoughtAt()).isNull();
        assertThat(result.getRestockedQuantity()).isNull();
        verify(restockPort).undoRestock(5L, "Pan", 2);
    }

    @Test
    void setBought_yaComprado_noRepiteLaReposicion() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(alreadyBoughtAndRestocked()));

        ShoppingItem result = service.setBought(7L, EMAIL, true);

        assertThat(result.isBought()).isTrue();
        verify(restockPort, never()).restock(any(), any(), anyInt());
        verify(repository, never()).save(any());
    }

    @Test
    void setBought_lanzaNotFoundSiElProductoNoEsDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setBought(99L, EMAIL, true))
                .isInstanceOf(ShoppingItemNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void delete_eliminaElProductoDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));

        service.delete(7L, EMAIL);

        verify(repository).deleteById(7L);
    }

    @Test
    void clearBought_devuelveCuantosSeQuitaron() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.deleteBoughtByHousehold(5L)).thenReturn(3);

        assertThat(service.clearBought(EMAIL)).isEqualTo(3);
    }
}