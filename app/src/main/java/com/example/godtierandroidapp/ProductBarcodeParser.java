package com.example.godtierandroidapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductBarcodeParser {
    public static void main(String[] args) {
        String barcodeData = "{Description=Product Description, Make=Product Make, Model=Product Model}";

        ProductData productData = parseProductBarcode(barcodeData);

        System.out.println("Description: " + productData.getDescription());
        System.out.println("Make: " + productData.getMake());
        System.out.println("Model: " + productData.getModel());
    }

    static ProductData parseProductBarcode(String textFileData) {
        String regex = "\\{Description=(.*?), Make=(.*?), Model=(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textFileData);

        if (matcher.matches()) {
            String description = matcher.group(1).trim();
            String make = matcher.group(2).trim();
            String model = matcher.group(3).trim();
            return new ProductData(description, make, model);
        } else {
            System.out.println("Invalid barcode data format");
            return null;
        }
    }
}

class ProductData {
    private String description;
    private String make;
    private String model;

    public ProductData(String description, String make, String model) {
        this.description = description;
        this.make = make;
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public boolean descEmpty() {
        return description.isEmpty();
    }

    public boolean makeEmpty() {
        return make.isEmpty();
    }
    public boolean modelEmpty() {
        return model.isEmpty();
    }
}
