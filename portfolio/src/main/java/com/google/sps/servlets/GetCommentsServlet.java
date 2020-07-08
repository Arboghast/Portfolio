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

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** returns all blog comments. */
@WebServlet("/get-comments")
public class GetCommentsServlet extends HttpServlet {

  private Translate translate; 

  public GetCommentsServlet(){
    translate = TranslateOptions.getDefaultInstance().getService();
  }
  
  /** 
   *   Obtain the users preferred comment limit from the query string and builds a FetchOption to
   * recieve that specific number of comments from the datastore.  
   *   The blog-title unique identifer filters the datastore and returns only comments relevant to 
   * the specified blog-title.
   *   Iterate through the datastore response and create an array of Comment objects, which are
   * able to be packed into a json and sent to the client.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Integer limit = paramToInteger(request, "limit", null);

    //withDefaults() returns all comments, else limit the number of comments returned
    FetchOptions queryLimit;
    if (limit == null) {
      queryLimit = FetchOptions.Builder.withDefaults();
    } else {
      queryLimit = FetchOptions.Builder.withLimit(limit);
    }

    String blogTitle = request.getParameter("blog-title");
    
    //filter for comments by blogpost
    FilterPredicate blogFilter = new FilterPredicate("blogTitle", EQUAL, blogTitle);

    Query query = new Query("Comment").setFilter(blogFilter)
        .addSort("timestamp", SortDirection.DESCENDING);

    String language = request.getParameter("language");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    //Convert datastore response to Gson compatable Comment objects
    //Blog title is not necessary in the response
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable(queryLimit)) {
      long id = entity.getKey().getId();
      String message =(String) entity.getProperty("message");
      if(language != "en") {
        message = translateComment(language,message);
      }
      long timestamp = (long) entity.getProperty("timestamp");
      float score = (float) entity.getProperty("score");

      Comment comment = new Comment(id, message, timestamp, score);
      comments.add(comment);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  /**
   * Function parses parameter value into an integer.
   * @param request - Frontend http request 
   * @param parameter - Key of desired value in query string
   * @param defaultValue - If no value is found
   * @return - Value associated with parameter in the request query string, or default value.
  */
  public Integer paramToInteger(HttpServletRequest request, String parameter, Integer defaultValue){
    String param = request.getParameter(parameter);
    if (param != null){
      return Integer.valueOf(param);
    }
    return defaultValue;
  }

  public String translateComment(String languageCode, String comment) {
    Translate.TranslateOption newLanguage = Translate.TranslateOption.targetLanguage(languageCode)
    String translatedText = translate.translate(comment, newLanguage).getTranslatedText();
    return translatedText;
  }

}
