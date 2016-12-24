package com.yyw.android.bestnow.data.event;

import android.util.ArrayMap;

import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.data.dao.EventDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yangyongwen on 16/12/15.
 */
@Singleton
public class EventRepository {


    private Map<String, Map<Long, Event>> eventCache;

    EventDao eventDao;


    @Inject
    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
//        eventCache = new ArrayMap<>();
    }

    public void saveEvents(List<Event> events, String date) {
//        Map<Long, Event> cache = listToMap(events);
//        eventCache.put(date, cache);
        cancelEventsSchedule(date);
        setEventsSchedule(events);


        eventDao.deleteInTx(getEvents(date));
        eventDao.insertOrReplaceInTx(events);
    }

    private void cancelEventsSchedule(String date){
        List<Event> events = getEvents(date);
        for (Event event : events) {
            cancelEventSchedule(event);
        }
    }

    private void cancelEventSchedule(Event event){
        if (event.getHasAlarm() != null && event.getHasAlarm() == true
                && (event.getDone() == null || event.getDone() == false)
                && event.getAlarmTime() != null) {
            EventScheduler.getInstance().cancelEventJob(event);
        }
    }

    private void setEventSchedule(Event event){
        if (event.getHasAlarm() != null && event.getHasAlarm() == true
                && (event.getDone() == null || event.getDone() == false)
                && event.getAlarmTime() != null) {
            EventScheduler.getInstance().addEventJob(event);
        }
    }

    private void setEventsSchedule(List<Event> events){
        for (Event event:events){
            setEventSchedule(event);
        }
    }

    public void saveEvent(Event event) {
//        Map<Long, Event> cache = eventCache.get(event.getDate());
//        if (cache != null) {
//            cache.put(event.getId(), event);
//        }
        cancelEventSchedule(event);

        eventDao.insertOrReplaceInTx(event);
    }

    public List<Event> getEvents(String date) {
//        Map<Long, Event> cache = eventCache.get(date);
//        if (cache != null) {
//            return mapToList(cache);
//        }
        List<Event> events = eventDao.queryBuilder()
                .where(EventDao.Properties.Date.eq(date))
                .list();
//        cache = listToMap(events);
//        eventCache.put(date, cache);
        return events;
    }

    private List<Event> mapToList(Map<Long, Event> events) {
        List<Event> result = new ArrayList<>();
        result.addAll(events.values());
        return result;
    }

    private Map<Long, Event> listToMap(List<Event> events) {
        Map<Long, Event> result = new ArrayMap<>();
        for (Event event : events) {
            result.put(event.getId(), event);
        }
        return result;
    }

    public void initEventSchedule() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());
        List<Event> todayEvents = getEvents(date);
        for (Event event : todayEvents) {
            if (event.getHasAlarm() != null && event.getHasAlarm() == true
                    && (event.getDone() == null || event.getDone() == false)
                    && event.getAlarmTime() != null) {
                EventScheduler.getInstance().addEventJob(event);
            }
        }
    }
}
