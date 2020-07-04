// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Randomly adds one of Sami's favorite quotes to the page
 */
function getRandomQuote() {
  const quotes =
      ['All work and no play makes Sami a dull boy.',
       'Just when I thought I was out, they pull me back in.', 
       'Hello. My name is Inigo Montoya. You killed my father. Prepare to die.',
       " You can't handle the truth!",
       'An idea is like a virus'];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote').getElementsByTagName('p')[0];
  quoteContainer.innerText = quote;
}
