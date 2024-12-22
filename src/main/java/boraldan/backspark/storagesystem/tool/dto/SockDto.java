package boraldan.backspark.storagesystem.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SockDto {

    private String model;
    private String color;
    private Integer cottonPercentage;
    private Integer quantity;
    private Boolean isActive;
}

