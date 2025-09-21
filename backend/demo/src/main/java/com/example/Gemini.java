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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@SpringBootApplication
@RestController
public class Gemini {
    @PostMapping("/api/recommend")
    public HashMap<String, String> returnData(@RequestBody recommendRequest request) throws IOException, URISyntaxException {
        String userInput = request.getInput();
        String suggestion = getDrinkSuggestion(userInput);
        System.out.println(userInput);
        System.out.println(suggestion);
        String details = fetchDrinkDetails(suggestion);
        String ingredients = Extract(details);
        System.out.println(ingredients);
        String instructions = Instruct(details);
        String imageLink = getImageLink(details); //extract image link
        System.out.println(imageLink);

        HashMap<String, String> hashData = new HashMap<>();
        hashData.put("name", suggestion);
        hashData.put("instructions", instructions);
        hashData.put("ingredients", ingredients);
        hashData.put("imageLink", imageLink);
        return hashData;
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        SpringApplication.run(Gemini.class, args);
    }

    public static String getDrinkSuggestion(String userInput) throws IOException {
        Client client = Client.builder()
            .apiKey("AIzaSyAPjd3gJCTTdFrLwMZcD7R56n1ku2svOho")
            .build();

        InputStream is = Gemini.class.getResourceAsStream("List.txt"); //pulls text from List.txt as string
        String list = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String prompt = """
                You are a drink recommender.
                Your task is to suggest a single non-alcoholic drink from the provided list that best matches the user's preference.
                You MUST choose a drink from the list. Do not make one up or choose one that isn't on the list.
                Respond with ONLY the drink's name and nothing else.
                """;
        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash",
            (prompt + "\n" + userInput + "\n" + list),
            null
        );

        return response.text(); 
    }

    public static String fetchDrinkDetails(String drinkName) throws IOException, URISyntaxException {
        String suggestionLink = drinkName.replace(' ', '+'); 

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
            instructions = matcherInst.group();
    
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
                        ingredientList.append(ingredient);
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

