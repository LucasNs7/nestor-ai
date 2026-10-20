package com.lucas.nestor_ai.infra.http.controller.transaction;

import com.lucas.nestor_ai.application.service.transaction.*;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionPatchRequest;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import com.lucas.nestor_ai.infra.http.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final ListTransactionsByCategoryUseCase listByCategoryUseCase;
    private final ListTransactionsByPeriodUseCase listByPeriodUseCase;
    private final ListTransactionsByDateUseCase listByDateUseCase;
    private final ListTransactionsByTypeUseCase listByTypeUseCase;
    private final DeleteTransactionByIdUseCase deleteByIdUseCase;
    private final UpdateTransactionUseCase updateUseCase;
    private final SaveTransactionUseCase saveUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        var transaction = saveUseCase.execute(request.toInput());

        return TransactionResponse.from(transaction);
    }

    @GetMapping("/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> findAllTransactionsByCategory(@PathVariable Category category) {
        return listByCategoryUseCase.execute(category).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/type/{type}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> findAllTransactionsByType(@PathVariable TransactionType type) {
        return listByTypeUseCase.execute(type).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> findAllTransactionsByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        return listByDateUseCase.execute(date).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> findAllTransactionsByPeriod(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start,

            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end
    ) {
        return listByPeriodUseCase.execute(start, end).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionPatchRequest request
    ) {
        var transaction = updateUseCase.execute(id, request.toInput());

        return TransactionResponse.from(transaction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable UUID id) {
        deleteByIdUseCase.execute(id);
    }
}
