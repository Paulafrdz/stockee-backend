package dev.paula.stockee_backend.stock;

import dev.paula.stockee_backend.lotes.LoteRepository;
import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository repository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private StockServiceImpl stockService;

    private UserEntity mockUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        return user;
    }

    @Test
    void getAll_ShouldReturnUserItems() {
        UserEntity user = mockUser();

        StockEntity item1 = new StockEntity();
        item1.setId(1L);

        when(currentUserService.get()).thenReturn(user);
        when(repository.findAllByUser(user)).thenReturn(List.of(item1));

        List<StockEntity> result = stockService.getAll();

        assertEquals(1, result.size());
        verify(repository).findAllByUser(user);
    }

    @Test
    void addItem_ShouldSetUserAndLastUpdate() {
        UserEntity user = mockUser();

        StockEntity input = new StockEntity();
        input.setName("Test");
        input.setCurrentStock(10.0);

        when(currentUserService.get()).thenReturn(user);
        when(repository.save(any(StockEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        StockEntity result = stockService.addItem(input);

        assertEquals(user, result.getUser());
        assertNotNull(result.getLastUpdate());
        verify(repository).save(input);
    }

    @Test
    void updateStock_ShouldUpdateValue() {
        UserEntity user = mockUser();

        StockEntity existing = new StockEntity();
        existing.setId(1L);
        existing.setCurrentStock(10.0);

        when(currentUserService.get()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        StockEntity result = stockService.updateStock(1L, 25.0);

        assertEquals(25.0, result.getCurrentStock());
        verify(repository).save(existing);
    }

    @Test
    void updateStock_WhenNotFound_ShouldThrow() {
        UserEntity user = mockUser();

        when(currentUserService.get()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> stockService.updateStock(1L, 20));

        assertEquals("Item no encontrado", ex.getMessage());
    }

    @Test
    void deleteItem_ShouldCallDelete() {
        UserEntity user = mockUser();

        StockEntity item = new StockEntity();
        item.setId(1L);

        when(currentUserService.get()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(item));

        stockService.deleteItem(1L);

        verify(repository).delete(item);
    }
}