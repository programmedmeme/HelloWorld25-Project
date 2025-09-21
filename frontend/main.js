// Handle "Get Recommendation"
function drinkRecommendation() {
  const mood = document.getElementById("mood").value.toLowerCase();
  if (!mood) return;

  // Switch screens via class toggle (not inline display)
  document.getElementById("input-screen").classList.remove("active");
  document.getElementById("recommendation-screen").classList.add("active");

  // Update the title dynamically
  document.querySelector(".second-title").textContent =
    `✩°⋆🌿. Your Drink Recommendation for: ${mood} ⋆⸜🍵✮˚`;
}

// Go back button
function goBack() {
  document.getElementById("recommendation-screen").classList.remove("active");
  document.getElementById("input-screen").classList.add("active");
}

// Modal logic
function openModal(drinkKey) {
  const recipes = {
    matcha: {
      name: "Matcha Latte",
      ingredients: [
        "4 mg matcha powder",
        "1 oz of water",
        "16 oz of milk",
        "2 oz of prefered sweetener"
      ]
    }
  };

  const drink = recipes[drinkKey.toLowerCase()];
  const modalBody = document.getElementById("modal-body");

  if (drink) {
    modalBody.innerHTML = `
      <h2>${drink.name}</h2>
      <ul>
        ${drink.ingredients.map(step => `<li>${step}</li>`).join("")}
      </ul>
    `;
  } else {
    modalBody.innerHTML = `<p>No recipe found.</p>`;
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
