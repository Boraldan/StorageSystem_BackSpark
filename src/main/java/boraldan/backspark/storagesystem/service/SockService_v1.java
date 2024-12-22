package boraldan.backspark.storagesystem.service;

import boraldan.backspark.storagesystem.domen.Sock;
import boraldan.backspark.storagesystem.repository.SockRepository;
import boraldan.backspark.storagesystem.service.api.SockService;
import boraldan.backspark.storagesystem.tool.builder.SockBuilder;
import boraldan.backspark.storagesystem.tool.dto.SockDto;
import boraldan.backspark.storagesystem.tool.builder.SockDtoBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SockService_v1 implements SockService {

    private final SockRepository sockRepository;

    public Page<Sock> getSocks(String color, Integer minCottonPercentage, Integer maxCottonPercentage, Pageable pageable) {
        log.info("Fetching socks with color={}, minCottonPercentage={}, maxCottonPercentage={}, page={}, size={}",
                color, minCottonPercentage, maxCottonPercentage, pageable.getPageNumber(), pageable.getPageSize());
        return sockRepository.findByColorAndCottonPercentageBetween(color, minCottonPercentage, maxCottonPercentage, pageable);
    }

    @Transactional
    public Sock registerIncome(SockDto sockDto) {
        log.info("Registering income: Model={}, Color={}, Cotton={}%, Quantity={}",
                sockDto.getModel(), sockDto.getColor(), sockDto.getCottonPercentage(), sockDto.getQuantity());

        Sock sock = sockRepository.findByModelAndColorAndCottonPercentage(
                        sockDto.getModel(),
                        sockDto.getColor(),
                        sockDto.getCottonPercentage())
                .orElse(null);

        if (sock != null) {
            sock.setQuantity(sock.getQuantity() + sockDto.getQuantity());
            log.info("Updated existing sock quantity: ID={}, NewQuantity={}", sock.getId(), sock.getQuantity());
        } else {
            sock = SockBuilder.getBuilder()
                    .model(sockDto.getModel())
                    .color(sockDto.getColor())
                    .cottonPercentage(sockDto.getCottonPercentage())
                    .quantity(sockDto.getQuantity())
                    .isActive(true)
                    .build();
            log.info("Created new sock: Model={}, Color={}, Cotton={}%, Quantity={}",
                    sockDto.getModel(), sockDto.getColor(), sockDto.getCottonPercentage(), sock.getQuantity());
        }

        return sockRepository.save(sock);
    }

    @Transactional
    public Sock registerIncome(String model, String color, Integer cottonPercentage, Integer quantity) {
        log.info("Registering income (alternate method): Model={}, Color={}, Cotton={}%, Quantity={}",
                model, color, cottonPercentage, quantity);

        SockDto sockDto = SockDtoBuilder.getBuilder()
                .model(model)
                .color(color)
                .cottonPercentage(cottonPercentage)
                .quantity(quantity)
                .isActive(true)
                .build();

        return registerIncome(sockDto);
    }

    @Transactional
    public Sock registerOutcome(SockDto sockDto) {
        log.info("Registering outcome: Model={}, Color={}, Cotton={}%, Quantity={}",
                sockDto.getModel(), sockDto.getColor(), sockDto.getCottonPercentage(), sockDto.getQuantity());

        Sock sock = sockRepository.findByModelAndColorAndCottonPercentage(sockDto.getModel(),
                        sockDto.getColor(),
                        sockDto.getCottonPercentage())
                .orElseThrow(() -> new IllegalArgumentException("This model is out of stock"));

        if (sock.getQuantity() < sockDto.getQuantity()) {
            log.error("Not enough socks in stock: Available={}, Requested={}", sock.getQuantity(), sockDto.getQuantity());
            throw new IllegalArgumentException("Not enough socks in stock");
        }

        sock.setQuantity(sock.getQuantity() - sockDto.getQuantity());
        log.info("Outcome registered successfully: Model={}, Color={}, Cotton={}%, RemainingQuantity={}",
                sockDto.getModel(), sockDto.getColor(), sockDto.getCottonPercentage(), sock.getQuantity());

        return sockRepository.save(sock);
    }

    public int getTotalSocks(String color, String operation, Integer cottonPercentage) {
        log.info("Calculating total socks: Color={}, Operation={}, CottonPercentage={}", color, operation, cottonPercentage);

        int minPercentage = 0;
        int maxPercentage = 100;

        switch (operation.toLowerCase()) {
            case "morethan":
                minPercentage = cottonPercentage + 1;
                break;
            case "lessthan":
                maxPercentage = cottonPercentage - 1;
                break;
            case "equal":
                minPercentage = cottonPercentage;
                maxPercentage = cottonPercentage;
                break;
        }

        int total = sockRepository.findTotalQuantity(color, cottonPercentage, minPercentage, maxPercentage).orElse(0);
        log.info("Total socks calculated: Color={}, Operation={}, CottonPercentage={}, Total={}",
                color, operation, cottonPercentage, total);

        return total;
    }

    @Transactional
    public Sock updateSock(UUID id, SockDto sockDto) {
        log.info("Updating sock: ID={}, NewModel={}, NewColor={}, NewCotton={}%, NewQuantity={}",
                id, sockDto.getModel(), sockDto.getColor(), sockDto.getCottonPercentage(), sockDto.getQuantity());

        Sock sock = sockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sock not found"));

        sock.setModel(sockDto.getModel());
        sock.setColor(sockDto.getColor());
        sock.setCottonPercentage(sockDto.getCottonPercentage());
        sock.setQuantity(sockDto.getQuantity());

        log.info("Sock updated successfully: ID={}, Model={}, Color={}, Cotton={}%, Quantity={}",
                id, sock.getModel(), sock.getColor(), sock.getCottonPercentage(), sock.getQuantity());

        return sockRepository.save(sock);
    }

    @Transactional
    public void uploadBatch(MultipartFile file) throws IOException {
        log.info("Uploading batch file: Name={}, Size={} bytes", file.getOriginalFilename(), file.getSize());

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            log.error("Unsupported file format: {}", file.getOriginalFilename());
            throw new IllegalArgumentException("Unsupported file format. Please upload an Excel file.");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String model = row.getCell(0).getStringCellValue();
                String color = row.getCell(1).getStringCellValue();
                int cottonPercentage = (int) row.getCell(2).getNumericCellValue();
                int quantity = (int) row.getCell(3).getNumericCellValue();

                if (cottonPercentage < 0 || cottonPercentage > 100 || quantity < 0) {
                    log.error("Invalid data in file: Row={}, Model={}, Color={}, Cotton={}, Quantity={}",
                            row.getRowNum(), model, color, cottonPercentage, quantity);
                    throw new IllegalArgumentException("Invalid data in file: " + row.getRowNum());
                }
                registerIncome(model, color, cottonPercentage, quantity);
                log.info("Batch entry added: Model={}, Color={}, Cotton={}%, Quantity={}",
                        model, color, cottonPercentage, quantity);
            }
        }

        log.info("Batch file processed successfully: Name={}", file.getOriginalFilename());
    }
}
