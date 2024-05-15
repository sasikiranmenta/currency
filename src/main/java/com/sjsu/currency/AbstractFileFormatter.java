package com.sjsu.currency;

import com.sjsu.currency.service.CurrencyConversion;

import java.util.List;

public abstract class AbstractFileFormatter implements FileFormatter {


    private final CurrencyConversion currencyConversion;

    public AbstractFileFormatter(CurrencyConversion currencyConversion) {
        this.currencyConversion = currencyConversion;
    }

    abstract List<Conversion> input(String inputFile);

    abstract void output(List<Conversion> data, String outfile);

    @Override
    public void calculateConversion(String inputFile, String outputFile) {
        List<Conversion> convertedTransactions = input(inputFile);
        currencyConversion.convert(convertedTransactions);
        output(convertedTransactions, outputFile);
    }
}
