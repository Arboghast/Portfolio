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

/** Servlet */
@WebServlet("/vote-comment")
public class VotesServlet extends HttpServlet {

  /**
   * parameters: Comment ID, Vote Type, page-name
   * 
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

        datastore.delete(commentEntityKey);
        datastore.put(commentEntity);

        String page = "/blogs/" + request.getParameter("page-name");
        response.sendRedirect(page);
    } 
    catch (EntityNotFoundException e) 
    {
        System.out.println(e);
    }

  }
}
