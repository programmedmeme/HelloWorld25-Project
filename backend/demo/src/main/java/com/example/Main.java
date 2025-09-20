package com.example;

    import com.google.genai.Client;
    import com.google.genai.types.GenerateContentResponse;

public class Main {
    public static void main(String[] args) {
        Client client = Client.builder()
            .apiKey("AIzaSyAPjd3gJCTTdFrLwMZcD7R56n1ku2svOho")
            .build();

        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash",
            "Explain how AI works in a few words",
            null
        );

        System.out.println(response.text());
    }
}