package com.yyw.android.bestnow.eventlist;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.data.event.EventRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 17/1/5.
 */
@Module
public class EventListModule {

    private EventListContract.View view;

    public EventListModule(EventListContract.View view) {
        this.view = view;
    }

    @Provides
    @FragmentScoped
    EventListContract.View providesAppDailyUsageContractView() {
        return view;
    }

    @Provides
    @FragmentScoped
    EventListContract.Model providesAppDailyUsageContractModel(EventRepository eventRepository) {
        return new EventListModel(eventRepository);
    }

}
