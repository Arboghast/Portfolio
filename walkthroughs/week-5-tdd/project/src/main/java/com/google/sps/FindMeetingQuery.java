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

package com.google.sps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

//Was able to get the runtime down from ~4.5 secs to ~2.8 secs by removing unnecessary loops(condensing the logic), then down to ~2.6 secs by simplifying conditionals
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) { 
    long meetingDuration = request.getDuration();
    final int WHOLE_DAY = 24*60;
    if(meetingDuration > WHOLE_DAY)
    {
        return new ArrayList<TimeRange>();
    }

    TimeRange initial = TimeRange.fromStartEnd(0,WHOLE_DAY,false); //Whole day
    Collection<TimeRange> validTimes = new ArrayList<TimeRange>();
    validTimes.add(initial);

    final Collection<Event> conflicts = new ArrayList<Event>();
    final Collection<String> wantedGuests = request.getAttendees();

    //Determine all mandatory attendees events
    for(final Event event: events){
        for(final String guest: event.getAttendees())
        {
             //Break on first guest found, no need to keep looking in the same event
            if(wantedGuests.contains(guest)) 
            {
                conflicts.add(event);
                break;
            }
        }
    }

    //Check for subsets
    for(final Event event: conflicts)
    {
        TimeRange eventTime = event.getWhen();
        for(final TimeRange valid : validTimes)
        {
            TimeRange timeBefore = TimeRange.fromStartEnd(valid.start(), eventTime.start(),false);
            TimeRange timeAfter = TimeRange.fromStartEnd(eventTime.end(),valid.end(),false);
            TimeRange updateRange;

            //Event is a subset of a valid time, which means we have to divide the valid time into two chunks
            if(valid.contains(eventTime)){

                validTimes.remove(valid);
                if(meetingDuration <= timeBefore.duration()){
                    validTimes.add(timeBefore);
                }
                if(meetingDuration <= timeAfter.duration()){
                    validTimes.add(timeAfter);
                }
                break;
            
            //event overlaps with a valid time, must alter the range of that valid time
            } else if(valid.overlaps(eventTime)){

                if(valid.contains(eventTime.start()))
                {
                    updateRange = timeBefore;
                }
                else{
                    updateRange = timeAfter;
                }

                validTimes.remove(valid);
                if(meetingDuration <= updateRange.duration())
                {
                    validTimes.add(updateRange);
                }
                break;
            }
        }
    }
    
    return validTimes;
  }
}
