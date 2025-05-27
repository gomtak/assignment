package com.wirebarley.api.account.adapters.in;

import com.wirebarley.api.account.application.port.in.AccountCreateUseCase;
import com.wirebarley.api.account.application.port.in.AccountDeleteUseCase;
import com.wirebarley.api.account.application.port.in.DepositUseCase;
import com.wirebarley.api.account.application.port.in.WithdrawUseCase;
import com.wirebarley.api.trasaction.adapters.dto.TransactionResponse;
import com.wirebarley.api.account.application.port.in.TransferUseCase;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;
    private final AccountCreateUseCase accountCreateUseCase;
    private final AccountDeleteUseCase accountDeleteUseCase;

    public AccountController(DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, TransferUseCase transferUseCase, AccountCreateUseCase accountCreateUseCase, AccountDeleteUseCase accountDeleteUseCase) {
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.transferUseCase = transferUseCase;
        this.accountCreateUseCase = accountCreateUseCase;
        this.accountDeleteUseCase = accountDeleteUseCase;
    }

    @PostMapping
    @Operation(summary = "계좌 생성", description = "계좌번호와 초기 잔액으로 새 계좌를 생성합니다.")
    public ResponseEntity<UUID> createAccount(
            @RequestParam(name = "accountNumber") String accountNumber,
            @RequestParam(name = "initialBalance") BigDecimal initialBalance
    ) {
        return ResponseEntity.ok().body(accountCreateUseCase.createAccount(accountNumber, initialBalance));
    }

    @DeleteMapping
    @Operation(summary = "계좌 삭제", description = "계좌 ID로 계좌를 삭제합니다.")
    public ResponseEntity<Void> deleteAccount(
            @RequestParam(name = "accountId") UUID accountId
    ) {
        accountDeleteUseCase.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "입금", description = "특정 계좌에 금액을 입금합니다.")
    public ResponseEntity<Void> deposit(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount
    ) {
        depositUseCase.deposit(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "출금", description = "특정 계좌에서 금액을 출금합니다.")
    public ResponseEntity<Void> withdraw(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount
    ) {
        withdrawUseCase.withdraw(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/transfer")
    @Operation(summary = "이체", description = "출금 계좌에서 다른 계좌로 금액을 이체합니다.")
    public ResponseEntity<Void> transfer(
            @PathVariable UUID accountId,
            @RequestParam(name = "to") UUID toAccountId,
            @RequestParam(name = "amount") BigDecimal amount
    ) {
        transferUseCase.transfer(accountId, toAccountId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "거래 내역 조회", description = "계좌의 거래 내역을 최신순으로 조회합니다.")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transferUseCase.getTransactions(accountId));
    }
}
