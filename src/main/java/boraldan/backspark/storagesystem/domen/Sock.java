package boraldan.backspark.storagesystem.domen;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_sock")
public class Sock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "model")
    private String model;

    @Column (name = "color")
    private String color;

    @Column (name = "cotton_percentage")
    private Integer cottonPercentage;

    @Column (name = "quantity")
    private Integer quantity;

    @Column (name = "is_active")
    private Boolean isActive;
}
