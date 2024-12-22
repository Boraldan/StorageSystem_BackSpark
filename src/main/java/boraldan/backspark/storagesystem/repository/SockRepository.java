package boraldan.backspark.storagesystem.repository;

import boraldan.backspark.storagesystem.domen.Sock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SockRepository extends JpaRepository<Sock, UUID> {

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage BETWEEN :minPercentage AND :maxPercentage")
    Optional<Integer> findTotalQuantity(String color, int cottonPercentage, int minPercentage, int maxPercentage);

    Optional<Sock> findByModelAndColorAndCottonPercentage(String model, String color, int cottonPercentage);

    Page<Sock> findByColorAndCottonPercentageBetween(String color, int minPercentage, int maxPercentage, Pageable pageable);
}

