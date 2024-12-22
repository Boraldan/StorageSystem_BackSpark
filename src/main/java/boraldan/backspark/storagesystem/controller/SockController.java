package boraldan.backspark.storagesystem.controller;


import boraldan.backspark.storagesystem.domen.Sock;
import boraldan.backspark.storagesystem.service.api.SockService;
import boraldan.backspark.storagesystem.tool.dto.SockDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/socks")
@Tag(name = "Socks API", description = "API для учета носков на складе")
public class SockController {

    private final SockService sockService;

    @Operation(summary = "Регистрация прихода носков", description = "Добавляет информацию о поступивших носках")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sock.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping("/income")
    public ResponseEntity<Sock> income(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.registerIncome(sockDto));
    }

    @Operation(summary = "Регистрация расхода носков", description = "Уменьшает количество носков на складе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sock.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping("/outcome")
    public ResponseEntity<Sock> registerOutcome(@RequestBody SockDto sockDto) {
        return ResponseEntity.ok(sockService.registerOutcome(sockDto));
    }

    @Operation(summary = "Получить все носки", description = "Возвращает список всех носков с фильтрацией по цвету и содержанию хлопка")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список носков",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<Sock>> getAll(
            @Parameter(description = "Цвет носков") @RequestParam(required = false, defaultValue = "green") String color,
            @Parameter(description = "Минимальный процент хлопка") @RequestParam(required = false, defaultValue = "0") Integer minCottonPercentage,
            @Parameter(description = "Максимальный процент хлопка") @RequestParam(required = false, defaultValue = "100") Integer maxCottonPercentage,
            @Parameter(description = "Номер страницы") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sockService.getSocks(color, minCottonPercentage, maxCottonPercentage, pageable));
    }

    @Operation(summary = "Получить общее количество носков", description = "Возвращает общее количество носков по фильтрам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Общее количество носков")
    })
    @GetMapping
    public ResponseEntity<Integer> getTotalSocks(
            @Parameter(description = "Цвет носков") @RequestParam(required = false, defaultValue = "green") String color,
            @Parameter(description = "Операция (больше, меньше, равно)") @RequestParam(required = false, defaultValue = "no") String operation,
            @Parameter(description = "Процент хлопка") @RequestParam(required = false, defaultValue = "0") Integer cottonPercentage) {
        return ResponseEntity.ok(sockService.getTotalSocks(color, operation, cottonPercentage));
    }

    @Operation(summary = "Обновить информацию о носках", description = "Обновляет данные о конкретных носках")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sock.class))),
            @ApiResponse(responseCode = "404", description = "Носки не найдены")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Sock> updateSock(@RequestBody SockDto sockDto,
                                           @PathVariable("id") UUID id) {
        return ResponseEntity.ok(sockService.updateSock(id, sockDto));
    }

    @Operation(summary = "Загрузка партии носков", description = "Загружает данные о носках из файла")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл успешно обработан"),
            @ApiResponse(responseCode = "400", description = "Ошибка в содержимом файла"),
            @ApiResponse(responseCode = "500", description = "Ошибка обработки файла")
    })
    @PostMapping("/batch")
    public ResponseEntity<String> uploadBatch(@RequestPart("file") MultipartFile file) {
        try {
            sockService.uploadBatch(file);
            return ResponseEntity.ok("Batch upload successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file.");
        }
    }
}