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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that adds a comment to our datastore when pinged.*/
@WebServlet("/add-comment")
public class AddCommentServlet extends HttpServlet {

  /**
   * Client sends comment data via POST and we create a comment Entity with that information.
   * We then add it to our datastore and force an update on the clientside via redirect.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userInput = request.getParameter("user-input");
    long timestamp = System.currentTimeMillis();
    String blogTitle = request.getParameter("blog-title");

    Document doc =
        Document.newBuilder().setContent(userInput).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
    double score = 0.3;

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("message", userInput);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("blogTitle", blogTitle);
    commentEntity.setProperty("score", score);
    commentEntity.setProperty("likes", 0);
    commentEntity.setProperty("dislikes", 0);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    String page = "/blogs/" + request.getParameter("page-name");
    response.sendRedirect(page);
  }
}
