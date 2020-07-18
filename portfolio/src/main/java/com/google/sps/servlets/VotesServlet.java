package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that increments the like or dislike count of a comment */
@WebServlet("/vote-comment")
public class VotesServlet extends HttpServlet {

  /**
   * parameters: Comment ID, Vote Type, page-name
   * Vote-type: either 'likes' or 'dislikes'
   *
   * Datastore does not allow us to modify/update properties of existing objects so
   * We work around that by creating a new comment object with all the same properties
   * Except the value we want to increment, which is changed by a factor of +1.
   * 
   * Old comment object is deleted and its clone is added to the datastore
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id = Long.parseLong(request.getParameter("key"));

    Key commentEntityKey = KeyFactory.createKey("Comment", id);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity comment; 

    try {
        comment = datastore.get(commentEntityKey);
        String voteType = request.getParameter("vote-type");

        int count = (int) (long) comment.getProperty(voteType);
        count++;
        
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("message", (String) comment.getProperty("message"));
        commentEntity.setProperty("timestamp", (long) comment.getProperty("timestamp"));
        commentEntity.setProperty("blogTitle", (String) comment.getProperty("blogTitle"));
        commentEntity.setProperty("score", (double) comment.getProperty("score"));

        int likes, dislikes;
        if(voteType.equals("likes")){
            likes = count;
            dislikes = (int) (long) comment.getProperty("dislikes");
        }else{
            likes = (int) (long) comment.getProperty("likes");
            dislikes= count;
        }
        commentEntity.setProperty("likes", likes);
        commentEntity.setProperty("dislikes", dislikes);

        //Deleting old comment object and adding the clone
        datastore.delete(commentEntityKey);
        datastore.put(commentEntity);

        //dynamic redirect
        String page = "/blogs/" + request.getParameter("page-name");
        response.sendRedirect(page);
    } 
    catch (EntityNotFoundException e) 
    {
        System.out.println(e);
    }

  }
}
