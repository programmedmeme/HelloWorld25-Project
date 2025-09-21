// Handle "Get Recommendation"
let recommendedDrinkName = "";
let recommendedDrinkInstructions = "";
let recommendedDrinkIngredients = "";
let recommendedDrinkImage = "";
function drinkRecommendation() {
    const mood = document.getElementById("mood").value.toLowerCase();
    
    if (!mood) return; 
    
    const data = {
      input: mood
    };

    fetch("http://localhost:8080/api/recommend", {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(responseData => {
        // Assuming your backend sends back an object like { drinkName: "...", instructions: "..." }
        recommendedDrinkName = responseData.name; 
        recommendedDrinkInstructions = responseData.instructions;
        recommendedDrinkIngredients = recommendedDrinkIngredients = responseData.ingredients.trim().split('\n');
        recommendedDrinkImage = responseData.imageLink;
        console.log(responseData);
        // Switch screens via class toggle (not inline display)
        document.getElementById("input-screen").classList.remove("active");
        document.getElementById("recommendation-screen").classList.add("active");

        // Update the title dynamically
        // --- CHANGE: Use the 'recommendedDrinkName' from the server response ---
        document.querySelector(".second-title").textContent =
            `✩°⋆🌿. Your Drink Recommendation: ${recommendedDrinkName} ⋆⸜🍵✮˚`;
        document.getElementById("instructions").textContent = recommendedDrinkInstructions;
        document.getElementById("drinkName").textContent = recommendedDrinkName;
        document.getElementById("mainImage").src = recommendedDrinkImage;
    }) // --- FIX: Added the closing parenthesis and brace for the .then() block
    .catch(error => console.error('Error:', error)); // It's also good practice to add a .catch()
}

// Go back button
function goBack() {
  document.getElementById("recommendation-screen").classList.remove("active");
  document.getElementById("input-screen").classList.add("active");
}

// Modal logic
function openModal() { // <-- Parameter 'drinkKey' is removed
    const modalBody = document.getElementById("modal-body");
  
    // Directly use the global variables. No need for the 'recipes' object.
    if (recommendedDrinkIngredients && recommendedDrinkIngredients.length > 0) {
      modalBody.innerHTML = `
        <h2>${recommendedDrinkName}</h2>
        <ul>
          ${recommendedDrinkIngredients.map(step => `<li>${step}</li>`).join("")}
        </ul>
      `;
    } else {
      modalBody.innerHTML = `<p>No recipe found for ${recommendedDrinkName}.</p>`;
    }
  
    document.getElementById("modal").style.display = "flex";
  }

function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// Allow Enter key to trigger recommendation
document.getElementById("mood").addEventListener("keydown", function(e) {
  if (e.key === "Enter") {
    drinkRecommendation();
  }
});