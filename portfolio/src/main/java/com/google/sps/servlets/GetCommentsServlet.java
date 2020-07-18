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

/** returns blog comments. */
@WebServlet("/get-comments")
public class GetCommentsServlet extends HttpServlet {

  /** 
   *   Obtain the users preferred comment limit from the query string and builds a FetchOption to
   * recieve that specific number of comments from the datastore.  
   *   The blog-title unique identifer filters the datastore and returns only comments relevant to 
   * the specified blog-title.
   *   If a language other than english is requested by the user, we will run the message through the translateMessage() function
   * and append that as the response instead
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
      if(!language.equals("en")) {
        message = translateComment(language,message);
      }
      long timestamp = (long) entity.getProperty("timestamp");
      double score = (double) entity.getProperty("score");

      int likes = (int) (long) entity.getProperty("likes");
      int dislikes = (int) (long) entity.getProperty("dislikes");

      Comment comment = new Comment(id, message, timestamp, score, likes, dislikes);
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

  /**
   * Inputs are the desired language and the content that we want to translate.
   * Returns the translated content.
   *
   * Uses the Google Translation API. 
  */
  public String translateComment(String languageCode, String comment) {
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translate.TranslateOption newLanguage = Translate.TranslateOption.targetLanguage(languageCode);
    String translatedText = translate.translate(comment, newLanguage).getTranslatedText();
    return translatedText;
  }

}
