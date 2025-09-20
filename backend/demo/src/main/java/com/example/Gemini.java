package com.example;
 
    import com.google.genai.Client;
    import com.google.genai.types.GenerateContentResponse;

public class Gemini {
     public static String getDrinkSuggestion(String userInput) {
        Client client = Client.builder()
            .apiKey("AIzaSyAPjd3gJCTTdFrLwMZcD7R56n1ku2svOho")
            .build();

        String list = "{\"drinks\":[{\"strDrink\":\"Afterglow\",\"strDrinkThumb\":\"https:\\/\\/www.thecocktaildb.com\\/images\\/media\\/drink\\/vuquyv1468876052.jpg\",\"idDrink\":\"12560\"}]}"; // shortened for example
        String prompt = "Suggest one singular drink from the list based on this mood, respond in only name: ";

        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash",
            (list + "\n" + prompt + "\n" + userInput),
            null
        );

        return response.text(); 
    }

    public static void main(String[] args) {
        String suggestion = getDrinkSuggestion("");
        System.out.println(suggestion);
    }
}

