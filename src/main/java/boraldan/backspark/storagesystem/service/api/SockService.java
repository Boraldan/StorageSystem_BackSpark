package boraldan.backspark.storagesystem.service.api;

import boraldan.backspark.storagesystem.domen.Sock;
import boraldan.backspark.storagesystem.tool.dto.SockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface SockService {

    Page<Sock> getSocks(String color, Integer minCottonPercentage, Integer maxCottonPercentage, Pageable pageable);

    Sock registerIncome(SockDto sockDto);

    Sock registerIncome(String model, String color, Integer cottonPercentage, Integer quantity);

    Sock registerOutcome(SockDto sockDto);

    int getTotalSocks(String color, String operation, Integer cottonPercentage);

    Sock updateSock(UUID id, SockDto sockDto);

    void uploadBatch(MultipartFile file) throws IOException;
}
