package com.wirebarley.api.account.application.port.in;

import java.util.UUID;

public interface AccountDeleteUseCase {
    void deleteAccount(UUID accountId);
}
