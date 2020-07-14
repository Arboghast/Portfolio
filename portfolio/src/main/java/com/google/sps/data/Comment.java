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

package com.google.sps.data;

public final class Comment {

  private final long id;
  private final String message;
  private final long timestamp;
  private final float score;
  private final int likes;
  private final int dislikes;
  //not necessary to store blogTitle, frontend already has that information

  /**
   * Object used to convert datastore data to json data.
   */
  public Comment(long id, String message, long timestamp, float score) {
    this.id = id;
    this.message = message;
    this.timestamp = timestamp;
    this.score = score;
    this.likes = 0;
    this.dislikes = 0;
  }
}
