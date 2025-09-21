package com.example;
 

//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args) throws IOException {
        String suggestion = getDrinkSuggestion("");
        System.out.println(suggestion);
        String imageLink;
    }
}

