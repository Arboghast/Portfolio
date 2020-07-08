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
  fetch('/random-quote')
      .then((response) => response.text())
      .then((quote) => {
        // Add it to the page.
        const quoteContainer = document.getElementById('quote').getElementsByTagName('p')[0];
        quoteContainer.innerText = quote;
      }).catch((err) => console.log(err));
}

/**
 * Sends blog-title and user's prefered comment limit to the server
 * to recieve all relevant comments
 *
 * Generates comment html using the recieved json and
 * a delete button to remove each comment via id
 */
function getComments() {
  const limitInput = document.getElementById('comment-limit');
  const value = limitInput.options[limitInput.selectedIndex].value;

  let blogTitle = document.getElementById('blog-title').innerHTML;
  blogTitle = blogTitle.split(' ').join('_'); // Clean the url
  let queryString = '/get-comments?' + 'blog-title=' + blogTitle;

  if (value != 'null') {
    queryString += '&limit=' + value;
  }

  const languageInput = document.getElementById('comment-language');
  const language = languageInput.options[languageInput.selectedIndex].value;
  queryString += '&language=' + language;


  fetch(queryString)
      .then((response) => response.json())
      .then((comments) => {
        const commentsContainer = document.getElementById('comments-container');
        commentsContainer.innerHTML = '';

        for (let i = comments.length-1; i >=0; i--) {
          const h4 = document.createElement('h4');
          const text = document.createTextNode(comments[i].message);
          h4.appendChild(text);

          const deleteButton = document.createElement('button');
          deleteButton.innerHTML = 'Delete Commment';
          deleteButton.setAttribute('onclick', 'deleteComment(' + comments[i].id + ')');
          h4.appendChild(deleteButton);

          const emotionImage = document.createElement('img');
          let imageUrl = scoreToImage(comments[i].score);
          emotionImage.setAttribute('src', imageUrl);
          h4.appendChild(emotionImage);

          commentsContainer.appendChild(h4);
        }
      }).catch((err) => console.log(err));
}

function scoreToImage(sentimentScore){
  let imageUrl = "images/sad.svg";
  if(sentimentScore >= .3){
    imageUrl = "images/cool.svg";
  } else if(sentimentScore >= .1){
    imageUrl = "images/smile.svg";
  } else if(sentimentScore >= -.1){
    imageUrl = "images/neutral.svg";
  } else if(sentimentScore >= -.3){
    imageUrl = "images/confused.svg";
  }

  return imageUrl;
}
/**
 * Adds the blog-title to a hidden form input so user's comments are added to the right blog
 */
function updateForm() {
  const blogTitle = document.getElementById('blog-title').innerHTML;
  document.getElementById('form-blog-title').value = blogTitle.split(' ').join('_');
}

/**
 *
 * @param {*} id - the id of the comment to be deleted
 * and then refetches the comments list
 */
function deleteComment(id) {
  const params = new URLSearchParams();
  params.append('key', id);
  fetch('/delete-comment', {method: 'POST', body: params})
      .then(()=> {
        getComments();
      }).catch((err)=>console.log(err));
}
