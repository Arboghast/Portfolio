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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long meetingDuration = request.getDuration();
    if(meetingDuration > 24*60)
    {
        return new ArrayList<TimeRange>();
    }

    TimeRange initial = TimeRange.fromStartEnd(0,24*60,false);
    Collection<TimeRange> validTimes = new ArrayList<TimeRange>();
    validTimes.add(initial);

    ArrayList<Event> conflicts = new ArrayList<Event>();
    ArrayList<Event> optional = new ArrayList<Event>();
    final Collection<String> wantedGuests = request.getAttendees();
    final Collection<String> optionalGuests = request.getOptionalAttendees();

    //determine all mandatory attendees events
    for(final Event event: events){
        for(final String guest: event.getAttendees())
        {
            if(wantedGuests.contains(guest))  //break on first guest found, no need to keep looking in the same event
            {
                conflicts.add(event);
                break;
            }
            if(optionalGuests.contains(guest))
            {
                optional.add(event);
                break;
            }
        }
    }

    if(conflicts.size() == 0){
        return validTimes;
    }

    ArrayList<Event> garbage = new ArrayList<Event>(); //to remove from conflicts arraylist once iteration is done, to prevent concurrentModificationException

    // check for subsets
    for(final Event event: conflicts)
    {
        TimeRange eventTime = event.getWhen();
        for(final TimeRange valid : validTimes)
        {
            if(valid.contains(eventTime)){ //event is a subset of a possible time, which means we have to divide the possible time into two chunks
                TimeRange timeBefore = TimeRange.fromStartEnd(valid.start(), eventTime.start(),false);
                TimeRange timeAfter = TimeRange.fromStartEnd(eventTime.end(),valid.end(),false);
                validTimes.remove(valid);

                if(timeBefore.duration() != 0 && meetingDuration <= timeBefore.duration()){
                    validTimes.add(timeBefore);
                }
                if(timeAfter.duration() != 0 && meetingDuration <= timeAfter.duration()){
                    validTimes.add(timeAfter);
                }
                garbage.add(event);
                break;
            }
        }
    }

    conflicts.removeAll(garbage);
    garbage.clear();

    if(conflicts.size() == 0){
        return validTimes;
    }

    // check for exclusive overlaps
    for(final Event event: conflicts)
    {
        TimeRange eventTime = event.getWhen();
        for(final TimeRange valid: validTimes)
        {
            if(valid.overlaps(eventTime)) //event overlaps with a valid time, must alter the range of that valid time
            {
                TimeRange updateRange;
                if(valid.contains(eventTime.start()))
                {
                    updateRange = TimeRange.fromStartEnd(valid.start(),eventTime.start(),false);
                }
                else{
                    updateRange = TimeRange.fromStartEnd(eventTime.end(),valid.end(),false);
                }
                validTimes.remove(valid);
                if(updateRange.duration() != 0 && meetingDuration <= updateRange.duration())
                {
                    validTimes.add(updateRange);
                }
                break;
            }
        }
    }

    // check for subsets
    for(final Event event: optional)
    {
        TimeRange eventTime = event.getWhen();
        for(final TimeRange valid : validTimes)
        {
            if(valid.contains(eventTime)){ //event is a subset of a possible time, which means we have to divide the possible time into two chunks
                TimeRange timeBefore = TimeRange.fromStartEnd(valid.start(), eventTime.start(),false);
                TimeRange timeAfter = TimeRange.fromStartEnd(eventTime.end(),valid.end(),false);
                validTimes.remove(valid);

                if(timeBefore.duration() != 0 && meetingDuration <= timeBefore.duration()){
                    validTimes.add(timeBefore);
                }
                if(timeAfter.duration() != 0 && meetingDuration <= timeAfter.duration()){
                    validTimes.add(timeAfter);
                }
                garbage.add(event);
                break;
            }
        }
    }

    return validTimes;
  }
}
