package com.example;
 

//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
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

public class Gemini {

     public static String getDrinkSuggestion(String userInput) throws IOException {
        Client client = Client.builder()
            .apiKey("AIzaSyAPjd3gJCTTdFrLwMZcD7R56n1ku2svOho")
            .build();

        InputStream is = Gemini.class.getResourceAsStream("List.txt"); //pulls text from List.txt as string
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

    public static void main(String[] args) throws IOException, URISyntaxException {
        String suggestion = getDrinkSuggestion("I have an exam tomorrow");
        String details = fetchDrinkDetails(suggestion);
        
        String instructions;

        //instructions
        Pattern patternInst = Pattern.compile("(?<=\\\"strInstructions\\\":\\\").*?(?=\\\",\\\"strInstructionsES\\\")");
        Matcher matcherInst = patternInst.matcher(details);

        if (matcherInst.find()) {
            instructions = matcherInst.group();
        } else {
            instructions = "";
        }

        System.out.println(suggestion);
        System.out.println(instructions);
    }
}

