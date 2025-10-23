package dev.paula.stockee_backend.orders;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private Integer itemCount;
    private List<OrderItemHistoryResponseDTO> items;
}