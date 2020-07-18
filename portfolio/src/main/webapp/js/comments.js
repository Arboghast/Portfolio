/**
 * @param: blog-title, comment limit, language
 * And returns all datastore comment entries that meet these requirments.
 *
 * Generates comment html using the response from the backend by using the createMessage() function
 *
 * Uses the GetCommentsServlet
 */
function getComments() {
    const limitInput = document.getElementById('comment-limit');
    const value = limitInput.options[limitInput.selectedIndex].value;
  
    let blogTitle = document.getElementById('blog-title').innerHTML;

    //Clean the url: Generic blog Title -> Generic_blog_title
    blogTitle = blogTitle.split(' ').join('_'); 
    let queryString = '/get-comments?' + 'blog-title=' + blogTitle;
  
    if (value != 'null') {
      queryString += '&limit=' + value;
    }
  
    const languageInput = document.getElementById('comment-language');
    const language = languageInput.options[languageInput.selectedIndex].value;
    queryString += '&language=' + language;
  
    //dynamically create html elements and append it to the webpage.
    fetch(queryString)
        .then((response) => response.json())
        .then((comments) => {
          const commentsContainer = document.getElementById('comments-container');
          commentsContainer.innerHTML = '';
  
          for (let i = comments.length-1; i >=0; i--) {
            const commentsDiv = createMessage(comments[i]);
            commentsContainer.appendChild(commentsDiv);
          }
        }).catch((err) => { //The no-comments html element
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
  
  /*
  * @param: a singluar comment object from the response payload.
  * Returns an efficiently generated html object
  *
  * HTML contains a message section, an img to match the message's sentiment score,
  * A like and dislike img button and counter for each, and a delete comment button.
  * These features are contained in certain divs to help style them with flexbox
  */
  function createMessage(comment){
      const commentsDiv = document.createElement('div');
      commentsDiv.setAttribute('class','comments-div');

      const messageDiv = document.createElement('div');
      messageDiv.setAttribute('class','message-div flex-row');  
      const h4 = document.createElement('h4');
      const text = document.createTextNode(comment.message);
      h4.appendChild(text);
      messageDiv.appendChild(h4);

      const emotionImage = document.createElement('img');
      let imageUrl = scoreToImage(comment.score);
      emotionImage.setAttribute('src', imageUrl);
      messageDiv.appendChild(emotionImage);
      commentsDiv.appendChild(messageDiv);

      const bottomDiv = document.createElement('div');
      bottomDiv.setAttribute('class','bottom-div flex-row');

      const thumbsDiv = document.createElement('div');
      thumbsDiv.setAttribute('class','thumbs-div flex-row');
      const likeImg = document.createElement('img');
      likeImg.setAttribute('onclick', "sendVote('likes'," + comment.id + ')');
      likeImg.setAttribute('src', "../images/emotions/thumb-up.svg");
      likeImg.setAttribute('style', "cursor: pointer");
      thumbsDiv.appendChild(likeImg);

      const likeVal = document.createTextNode(comment.likes);
      thumbsDiv.appendChild(likeVal);

      const dislikeImg = document.createElement('img');
      dislikeImg.setAttribute('onclick', "sendVote('dislikes'," + comment.id + ')');
      dislikeImg.setAttribute('src', "../images/emotions/thumb-down.svg");
      dislikeImg.setAttribute('style', "cursor: pointer");
      thumbsDiv.appendChild(dislikeImg);
      bottomDiv.appendChild(thumbsDiv);

      const dislikeVal = document.createTextNode(comment.dislikes);
      thumbsDiv.appendChild(dislikeVal);

      const deleteButton = document.createElement('button');
      deleteButton.innerHTML = 'Delete Commment';
      deleteButton.setAttribute('onclick', 'deleteComment(' + comment.id +')');
      bottomDiv.appendChild(deleteButton);
      commentsDiv.appendChild(bottomDiv);

      return commentsDiv;
  }
  
  /*
  * @param: a number from -1.00 to 1.00
  * Returns an image corresponding to the sentiment score of the message,
  * which was calculated by the Google Sentiment Analysis API 
  */
  function scoreToImage(sentimentScore){
    const path = "../images/emotions/";
    let imageUrl = path + "sad.svg";
    if(sentimentScore >= .3){
      imageUrl = path + "cool.svg";
    } else if(sentimentScore >= .1){
      imageUrl = path + "smile.svg";
    } else if(sentimentScore >= -.1){
      imageUrl = path + "neutral.svg";
    } else if(sentimentScore >= -.3){
      imageUrl = path + "confused.svg";
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
   * and if sucessful, refetches the comments.
   *
   * Uses the DeleteCommentsServlet
   */
  function deleteComment(id) {
    const params = new URLSearchParams();
    const pageName = document.getElementById("page-name").value;
    params.append('key', id);
    params.append('page-name',pageName);

    fetch('/delete-comment', {method: 'POST', body: params})
        .then(()=> {
          getComments();
        }).catch((err)=>console.log(err));
  }
