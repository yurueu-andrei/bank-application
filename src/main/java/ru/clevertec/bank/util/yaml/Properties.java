package ru.clevertec.bank.util.yaml;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class Properties {

    private PostgresProperties postgres;
    private BigDecimal interestRate;
    private Map<String, BigDecimal> exchangeRates;
}
