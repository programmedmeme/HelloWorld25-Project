package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiFixedFormatting {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String suggestion = getDrinkSuggestion("It's a cold day. I want something warm."); //user input, Gemini output
        String details = fetchDrinkDetails(suggestion); //Gemini output into link, fetch details from CocktailDB
        String ingredients = Extract(details); //extract ingredient list from details
        String instructions = Instruct(details); //extract instructions from ingredient list
        String imageLink = getImageLink(details); //extract image link

        //result print, temporary to see how code works
        System.out.println("\n" + suggestion);
        System.out.println(imageLink);
        System.out.println(ingredients);
        System.out.println(instructions);
        System.out.println("\n" + details);
    }

    public static String getDrinkSuggestion(String userInput) throws IOException {
        Client client = Client.builder()
            .apiKey("AIzaSyAPjd3gJCTTdFrLwMZcD7R56n1ku2svOho") //Gemini API key
            .build();

        InputStream is = GeminiFixedFormatting.class.getResourceAsStream("List.txt"); //pulls text from List.txt as string
        String list = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String prompt = "Respond with only name, suggest one singular drink from the list based on this mood: ";

        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash",
            (list + "\n" + prompt + "\n" + userInput),
            null
        );

        return response.text(); 
    }

    public static String fetchDrinkDetails(String drinkName) throws IOException, URISyntaxException {
        String suggestionLink = drinkName.replaceAll(" ", "+");

        String urlString = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" 
                            + suggestionLink.toLowerCase();

        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
             response = new StringBuilder();
             String line;
             while ((line = reader.readLine()) != null) {
                 response.append(line);
             }
        }

        return response.toString();
    }

    public static String Instruct(String details) throws IOException, URISyntaxException {

        String instructions = "";

        //instructions
        Pattern patternInst = Pattern.compile("(?<=\\\"strInstructions\\\":\\\").*?(?=\\\",\\\"strInstructionsES\\\")");
        Matcher matcherInst = patternInst.matcher(details);

        if (matcherInst.find()) {
            instructions = matcherInst.group().replaceAll("\\\\", "");
    
        } else {
            instructions = "";
        }

        return instructions;
    }

    public static String Extract(String details) throws IOException, URISyntaxException {
        
        StringBuilder ingredientList = new StringBuilder();

        JsonObject json = JsonParser.parseString(details).getAsJsonObject();
        JsonArray drinks = json.getAsJsonArray("drinks");

        for (int i = 0; i < drinks.size(); i++) {
            JsonObject drink = drinks.get(i).getAsJsonObject();

            for (int j = 1; j <= 15; j++) {
                String ingredientKey = "strIngredient" + j;
                String measureKey = "strMeasure" + j;

                if (drink.has(ingredientKey) && !drink.get(ingredientKey).isJsonNull()) {
                    String ingredient = drink.get(ingredientKey).getAsString().trim();
                    String measure = drink.has(measureKey) && !drink.get(measureKey).isJsonNull() ? drink.get(measureKey).getAsString().trim() : "";
                    if (!ingredient.isEmpty()) {
                        ingredientList.append("- ").append(ingredient);
                        if (!measure.isEmpty()) {
                            ingredientList.append(" (").append(measure).append(")");
                        }
                        ingredientList.append("\n");
                    }
                }
            }
        }

        return ingredientList.toString().replaceAll("\\\\", "");
    }

    public static String getImageLink(String details) {
        String imageLink;

        Pattern patternImg = Pattern.compile("(?<=\\\"strDrinkThumb\\\":\\\").*?(?=\\\",\\\"strIngredient1\\\")");
        Matcher matcherImg = patternImg.matcher(details);

        if (matcherImg.find()) {
            imageLink = matcherImg.group().replaceAll("\\\\", "");
    
        } else {
            imageLink = "";
        }

        return imageLink;
    }
}

