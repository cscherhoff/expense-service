package com.exxeta.expenseservice.dtos;

import java.math.BigDecimal;
import java.util.Objects;

public class TransferDto {

    private final String userId;
    private final String accountName;
    private final BigDecimal amount;

    public TransferDto(String userId, String accountName, BigDecimal amount) {
        this.userId = userId;
        this.accountName = accountName;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransferDto that = (TransferDto) o;
        return userId == that.userId &&
            Objects.equals(accountName, that.accountName) &&
            Objects.equals(amount, that.amount);
    }

    public String getUserId() {
        return userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
