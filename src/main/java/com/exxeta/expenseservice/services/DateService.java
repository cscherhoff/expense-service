package com.exxeta.expenseservice.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateService {

    public int getRealCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }
}
