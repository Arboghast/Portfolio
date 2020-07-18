/**
 * Randomly adds one of Sami's favorite quotes to the page
 *
 * Uses the QuotesServlet
 */
function getRandomQuote() {
  fetch('/random-quote')
      .then((response) => response.text())
      .then((quote) => {
        const quoteContainer = document.getElementById('quote').getElementsByTagName('p')[0];
        quoteContainer.innerText = quote;
      }).catch((err) => console.log(err));
}
