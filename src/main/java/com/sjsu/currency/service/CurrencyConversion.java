package com.sjsu.currency.service;

import com.sjsu.currency.Conversion;

import java.util.List;

public interface CurrencyConversion {
    void convert(List<Conversion> parsedData);
}
