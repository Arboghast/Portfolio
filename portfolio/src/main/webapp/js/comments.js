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
    blogTitle = blogTitle.split(' ').join('_'); // Clean the url: Generic blog Title -> Generic_blog_title
    let queryString = '/get-comments?' + 'blog-title=' + blogTitle;
  
    if (value != 'null') {
      queryString += '&limit=' + value;
    }
  
    const languageInput = document.getElementById('comment-language');
    const language = languageInput.options[languageInput.selectedIndex].value;
    queryString += '&language=' + language;
  
    //dynamically creating html elements using the response from the getComments api and appending it to the webpage
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
      thumbsDiv.setAttribute('class','thumbs-div flex-row')  
      const likeImg = document.createElement('img');
      likeImg.setAttribute('onclick', 'addLike(' + this + ')');
      likeImg.setAttribute('src', "../images/emotions/thumb-up.svg")
      thumbsDiv.appendChild(likeImg);

      const likeVal = document.createTextNode('0');
      thumbsDiv.appendChild(likeVal);

      const dislikeImg = document.createElement('img');
      dislikeImg.setAttribute('onclick', 'addDislike(' + this + ')');
      dislikeImg.setAttribute('src', "../images/emotions/thumb-down.svg")
      thumbsDiv.appendChild(dislikeImg);
      bottomDiv.appendChild(thumbsDiv);

      const dislikeVal = document.createTextNode('0');
      thumbsDiv.appendChild(dislikeVal);

      const deleteButton = document.createElement('button');
      deleteButton.innerHTML = 'Delete Commment';
      deleteButton.setAttribute('onclick', 'deleteComment(' + comment.id +')');
      bottomDiv.appendChild(deleteButton);
      commentsDiv.appendChild(bottomDiv);

      return commentsDiv;
  }
  
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
   * and then refetches the comments list
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
