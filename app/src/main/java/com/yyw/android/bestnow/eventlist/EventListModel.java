package com.yyw.android.bestnow.eventlist;

import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.data.event.EventRepository;

import java.util.List;

import rx.Observable;

/**
 * Created by yangyongwen on 17/1/5.
 */

public class EventListModel implements EventListContract.Model {

    private EventRepository eventRepository;

    public EventListModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Observable<List<Event>> queryEvents(String date) {
        return eventRepository.getEventsObservable(date);
    }

    @Override
    public void saveEvents(List<Event> events, String date) {
        eventRepository.saveEvents(events, date);
    }

    @Override
    public void cleanUp() {
        eventRepository = null;
    }
}
