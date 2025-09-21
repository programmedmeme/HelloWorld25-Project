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
      instructions: [
        "Pour hot water into your matcha bowl to warm it, then discard.",
        "Sift matcha powder into the bowl.",
        "Add a small amount of hot (not boiling) water.",
        "Whisk until smooth and frothy.",
        "Add milk and sweetener, whisk again, and enjoy!"
      ]
    }
  };

  const drink = recipes[drinkKey.toLowerCase()];
  const modalBody = document.getElementById("modal-body");

  if (drink) {
    modalBody.innerHTML = `
      <h2>${drink.name}</h2>
      <ul>
        ${drink.instructions.map(step => `<li>${step}</li>`).join("")}
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
