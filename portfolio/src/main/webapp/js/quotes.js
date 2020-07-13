/**
 * Randomly adds one of Sami's favorite quotes to the page
 */
function getRandomQuote() {
  fetch('/random-quote')
      .then((response) => response.text())
      .then((quote) => {
        // Add it to the page.
        const quoteContainer = document.getElementById('quote').getElementsByTagName('p')[0];
        quoteContainer.innerText = quote;
      }).catch((err) => console.log(err));
}