package com.google.sps.data;

/**
 * Object used to convert datastore data to json data.
 */
public final class Comment {

  private final long id;
  private final String message;
  private final long timestamp;
  private final double score;
  private final int likes;
  private final int dislikes;
  //not necessary to store blogTitle, frontend already has that information

   public Comment(long id, String message, long timestamp, double score, int likes, int dislikes) {
    this.id = id;
    this.message = message;
    this.timestamp = timestamp;
    this.score = score;
    this.likes = likes;
    this.dislikes = dislikes;
  }
}
