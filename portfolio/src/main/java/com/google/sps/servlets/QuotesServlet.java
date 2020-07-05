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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/random-quote")
public class QuotesServlet extends HttpServlet {

  private List<String> quotes;

  @Override
  public void init() {
    quotes = new ArrayList<String>();
    quotes.add("All work and no play makes Sami a dull boy");
    quotes.add("Just when I thought I was out, they pull me back in");
    quotes.add("You can't handle the truth!");
    quotes.add("An idea is like a virus");
    quotes.add("Proximity to power deludes most into thinking they wield it");
    quotes.add("There’s no better way to overpower a trickle of doubt than with a flood of naked truth");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");

    //Select random quote
    String quote = quotes.get((int) (Math.random() * quotes.size()));
    response.getWriter().println(quote);
  }
}