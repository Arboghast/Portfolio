package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Server that returns a random quote when pinged. */
@WebServlet("/random-quote")
public class QuotesServlet extends HttpServlet {

  private List<String> quotes;

  @Override
  public void init() {
    quotes = new ArrayList<String>();
    quotes.add("All work and no play makes Sami a dull boy"); //The Shining
    quotes.add("Just when I thought I was out, they pull me back in"); //Godfather
    quotes.add("You can't handle the truth!"); //A Few Good Men
    quotes.add("An idea is like a virus"); //Inception
    quotes.add("Proximity to power deludes most into thinking they wield it"); //House of Cards
    quotes.add("There’s no better way to overpower a trickle of doubt than with a flood of naked truth"); //?
  }

  /**
   * Chooses a random quote from the hard-coded quotes ArrayList and sends it to the client.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");

    String quote = quotes.get((int) (Math.random() * quotes.size()));
    response.getWriter().println(quote);
  }
}
