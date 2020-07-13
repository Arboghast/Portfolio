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
            const commentsDiv = document.createElement('div');
            commentsDiv.setAttribute('class','comments-div');
  
            const h4 = document.createElement('h4');
            const text = document.createTextNode(comments[i].message);
            h4.appendChild(text);
            commentsDiv.appendChild(h4);
  
            const deleteButton = document.createElement('button');
            deleteButton.innerHTML = 'Delete Commment';
            deleteButton.setAttribute('onclick', 'deleteComment(' + comments[i].id + ')');
            commentsDiv.appendChild(deleteButton);
  
            const emotionImage = document.createElement('img');
            let imageUrl = scoreToImage(comments[i].score);
            emotionImage.setAttribute('src', imageUrl);
            commentsDiv.appendChild(emotionImage);
  
            commentsContainer.appendChild(commentsDiv);
  
          }
        }).catch((err) => {
          const commentsContainer = document.getElementById('comments-container');
          commentsContainer.innerHTML = '';
  
          const commentsDiv = document.createElement('div');
          commentsDiv.setAttribute('class','no-comments-div');
  
          const h4 = document.createElement("h4");
          const text = document.createTextNode("No Comments");
          h4.appendChild(text);
  
          commentsDiv.appendChild(h4);
          commentsContainer.appendChild(commentsDiv);
        });
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
  