package com.sjsu.currency;

import com.sjsu.currency.service.CurrencyConversion;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component("json")
public class JSONFileFormatter extends AbstractFileFormatter {

    public JSONFileFormatter(CurrencyConversion currencyConversion) {
        super(currencyConversion);
    }

    @Override
    List<Conversion> input(String inputFile) {
        try {
            String data = Files.readString(Paths.get(inputFile));
            JSONObject json = new JSONObject(data);
            JSONArray jsonArray = json.getJSONArray("transactions");
            List<Conversion> parsedList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject raw = jsonArray.getJSONObject(i);
                Conversion conversion = new Conversion();
                conversion.setFrom(raw.getString("OriginalCurrency"));
                conversion.setTo(raw.getString("TargetCurrency"));
                conversion.setTransactionPrice(raw.getDouble("Amount"));
                parsedList.add(conversion);
            }
            return parsedList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void output(List<Conversion> data, String outfile) {
        try (FileWriter writer = new FileWriter(outfile)) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (Conversion conversion : data) {
                JSONObject node = new JSONObject();
                fillNode(node, conversion);
                jsonArray.put(node);
            }

            jsonObject.put("transactions", jsonArray);
            jsonObject.write(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillNode(JSONObject node, Conversion conversion) {
        try {
            node.put("Amount", conversion.getTransactionPrice());
            node.put("OriginalCurrency", conversion.getFrom());
            node.put("TargetCurrency", conversion.getTo());

            if (conversion.getExceptionMessage() == null) {
                double convertedAmount = conversion.getConvertedPrice();
                String formatAmount = String.format("%.2f", convertedAmount);
                node.put("ConvertedAmount", formatAmount);
                node.put("Status", "Success");
            } else {
                node.put("ConvertedAmount", "");
                node.put("Status", conversion.getExceptionMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}