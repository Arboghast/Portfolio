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
  fetch("/random-quote")
  .then(response => response.text())
  .then(quote => {
    // Add it to the page.
    const quoteContainer = document.getElementById('quote').getElementsByTagName('p')[0];
    quoteContainer.innerText = quote;
  }).catch(err => console.log(err))
}

function getComments() {
    const limitInput = document.getElementById("comment-limit");
    const value = limitInput.options[limitInput.selectedIndex].value;
    let queryString = "/get-comments";
    if(value != "null")
    {
        queryString += "?limit=" + value;
    }

    fetch(queryString)
    .then(response => response.json())
    .then(comments => {
        const commentsContainer = document.getElementById('comments-container');
        commentsContainer.innerHTML = "";

        for(let i = comments.length-1; i >=0; i--)
        {
            let h4 = document.createElement("h4");
            let text = document.createTextNode(comments[i].message);
            h4.appendChild(text);

            let deleteButton = document.createElement("button");
            deleteButton.innerHTML = "Delete Commment";
            deleteButton.setAttribute("onclick","deleteComment(" + comments[i].id + ")");
            h4.appendChild(deleteButton);

            commentsContainer.appendChild(h4);
        }
    }).catch(err => console.log(err))
}

function addTitleToForm() {
    const form = document.getElementById("form");
    const blogTitle = document.getElementById("blog-title").innerHTML;
    form.append("blog-title", blogTitle);
}


function deleteComment(id) {
    const params = new URLSearchParams();
    params.append('key', id);
    fetch('/delete-comment', {method: 'POST', body: params})
    .then(()=> {
        getComments();
    }).catch(err=>console.log(err))
    
}