package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that deletes a specific comment by id. */
@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  /**
   * The client sends us a comment id through POST and we remove that comment from
   * the datastore via id. We then force an update on the clientside via sendRedirect().
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {	
    long id = Long.parseLong(request.getParameter("key"));

    //.delete() to remove from the datastore
    Key commentEntityKey = KeyFactory.createKey("Comment", id);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(commentEntityKey);
    
    //Dynamic Redirect
    String page = "/blogs/" + request.getParameter("page-name");
    response.sendRedirect(page);
  }
}
