package com.sjsu.currency.service;

import com.sjsu.currency.Conversion;
import com.sjsu.currency.TransactionError;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CurrencyConversionImpl implements CurrencyConversion {

    private final Map<String, Map<String, Double>> conversionMap;
    private final Set<String> allowedCurrencies;

    public CurrencyConversionImpl() {
        this.conversionMap = initializeConversionMap();
        this.allowedCurrencies = buildAllowedCurrenciesSet();
    }

    @Override
    public void convert(List<Conversion> parsedData) {
        parsedData.forEach(transaction -> {
            try {
                validateCurrencyCodes(transaction);
                Double conversionRate = determineConversionRate(transaction);
                transaction.setConvertedPrice(transaction.getTransactionPrice() * conversionRate);
            } catch (Exception e) {
                transaction.setExceptionMessage(e.getMessage());
            }
        });
    }

    private void validateCurrencyCodes(Conversion transaction) throws TransactionError {
        if (!allowedCurrencies.contains(transaction.getFrom())) {
            throw new TransactionError("Invalid original currency code: " + transaction.getFrom());
        }
        if (!allowedCurrencies.contains(transaction.getTo())) {
            throw new TransactionError("Invalid target currency code: " + transaction.getTo());
        }
    }

    private Double determineConversionRate(Conversion transaction) throws TransactionError {
        String fromCurrency = transaction.getFrom();
        String toCurrency = transaction.getTo();

        if (conversionMap.containsKey(fromCurrency)) {
            Map<String, Double> innerMap = conversionMap.get(fromCurrency);
            if (innerMap.containsKey(toCurrency)) {
                return innerMap.get(toCurrency);
            }
        }
        throw new TransactionError("No Conversion Rate Provided for " + fromCurrency + " to " + toCurrency);
    }

    private Map<String, Map<String, Double>> initializeConversionMap() {
        Map<String, Map<String, Double>> conversionTable = new HashMap<>();
        addConversionRate(conversionTable, "USD", "EUR", 0.94);
        addConversionRate(conversionTable, "EUR", "GBP", 0.86);
        addConversionRate(conversionTable, "GBP", "INR", 103.98);
        addConversionRate(conversionTable, "AUD", "CAD", 0.89);
        addConversionRate(conversionTable, "CAD", "USD", 0.73);
        addConversionRate(conversionTable, "CHF", "AUD", 1.69);
        addConversionRate(conversionTable, "USD", "CHF", 0.91);
        addConversionRate(conversionTable, "JPY", "USD", 0.0065);
        return conversionTable;
    }

    private void addConversionRate(Map<String, Map<String, Double>> conversionTable, String fromCurrency, String toCurrency, Double rate) {
        conversionTable.putIfAbsent(fromCurrency, new HashMap<>());
        conversionTable.get(fromCurrency).put(toCurrency, rate);
    }

    private Set<String> buildAllowedCurrenciesSet() {
        Set<String> allowedCurrencies = new HashSet<>();
        conversionMap.forEach((fromCurrency, toCurrencies) -> {
            allowedCurrencies.add(fromCurrency);
            toCurrencies.keySet().forEach(allowedCurrencies::add);
        });
        return allowedCurrencies;
    }
}