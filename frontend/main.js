function drinkRecommendation() {
    const mood = document.getElementById("mood").value.toLowerCase();
    const data = {
        mood: mood
    }

    fetch("/api/recommend", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })  
    .then(response => response.text()) // Get the response from the server as plain text
    .then(responseText => {
        // Display the response message on the page
        document.getElementById('').innerText = responseText;
    })
    .catch(error => console.error('Error:', error));

    //This makes the result scroll down
    const result = document.getElementById("drink-1");
    result.scrollIntoView({behavior: "smooth" });
}


