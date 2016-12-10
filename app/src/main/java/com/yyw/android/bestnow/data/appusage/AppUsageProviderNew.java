package com.yyw.android.bestnow.data.appusage;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by samsung on 2016/12/7.
 */

public class AppUsageProviderNew {
    private static final String TAG = LogUtils.makeLogTag(AppUsageProviderNew.class);
    private static final String LAST_EVENT_TYPE = "last_event_type";
    private static final String LAST_EVENT_PACKAGE = "last_event_package";
    private static final String LAST_EVENT_TIME_STAMP="last_event_time_stamp";
    private static final String LAST_UPDATE_TIME="last_update_time";
    private static final int MOVE_TO_BACKGROUND= UsageEvents.Event.MOVE_TO_BACKGROUND;
    private static final int MOVE_TO_FOREGROUND= UsageEvents.Event.MOVE_TO_FOREGROUND;

    Context context;
    UsageRepository repository;
    Map<String, PackageInfo> userInstallApp;
    int lastEventType;
    String lastEventPackage;
    long lastEventTimeStamp;
    SPUtils spUtils;
    long lastUpdateTime;

    AppUsageProviderNew(Context context, UsageRepository repository, SPUtils spUtils) {
        this.context = context;
        this.repository = repository;
        this.spUtils = spUtils;
        userInstallApp = Utils.getUserInstalledPackageInfo(context);
        lastEventType = spUtils.getIntValue(LAST_EVENT_TYPE, -1);
        lastEventPackage = spUtils.getStringValue(LAST_EVENT_PACKAGE, "");
        long currentTime=System.currentTimeMillis();
        lastEventTimeStamp=spUtils.getLongValue(LAST_EVENT_TIME_STAMP,currentTime);
        lastUpdateTime=spUtils.getLongValue(LAST_UPDATE_TIME,currentTime);
    }

    public Map<String, AppUsage> getAppUsageBetween(long startTime, long endTime) {
//        return computeAppUsage(startTime, endTime);

        return computeUsage(lastUpdateTime,System.currentTimeMillis());
    }

//    private Map<String, AppUsage> computeAppUsage(long startTime, long endTime) {
//        Map<String, AppUsage> appUsageMap = new ArrayMap<>();
//        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        UsageEvents events = manager.queryEvents(startTime, endTime);
//
//        MyEvent startEvent = new MyEvent();
//        startEvent.timeStamp = startTime;
//
//        MyEvent endEvent = new MyEvent();
//        endEvent.timeStamp = startTime;
//
//        UsageEvents.Event event = new UsageEvents.Event();
//
//
//        LogUtils.d(TAG, "start time: " + DateUtils.formatTime(startTime));
//
//        while (events.getNextEvent(event)) {
//            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
//                    && !event.getPackageName().equals(startEvent.packageName)) {
//
//                if (endEvent.packageName != null) {
//                    AppUsage appUsage = genAppUsage(startEvent, endEvent);
//                    if (appUsage != null) {
//                        AppUsage oldAppUsage = appUsageMap.get(appUsage.getPackageName());
//                        if (oldAppUsage == null) {
//                            appUsageMap.put(appUsage.getPackageName(), appUsage);
//                        } else {
//                            appUsageMap.put(appUsage.getPackageName(), AppUsage.combine(appUsage, oldAppUsage));
//                        }
//                    }
//                }
//
//                startEvent.timeStamp = event.getTimeStamp();
//                startEvent.eventType = UsageEvents.Event.MOVE_TO_FOREGROUND;
//                startEvent.packageName = event.getPackageName();
//            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
//                endEvent.timeStamp = event.getTimeStamp();
//                endEvent.eventType = UsageEvents.Event.MOVE_TO_BACKGROUND;
//                endEvent.packageName = event.getPackageName();
//            }
//
//            LogUtils.d(TAG, "package: " + event.getPackageName() + " event type: " + event.getEventType() + " time:" + DateUtils.formatTime(event.getTimeStamp()));
//        }
//        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {   // 主要为了计算当前app的使用时间，由于你一直在该app中，没有退出，所以上面那段代码不会计算当前app的使用时间。
//            startEvent.timeStamp = event.getTimeStamp();
//            startEvent.eventType = UsageEvents.Event.MOVE_TO_FOREGROUND;
//            startEvent.packageName = event.getPackageName();
//
//            endEvent.timeStamp = endTime;
//            endEvent.eventType = UsageEvents.Event.MOVE_TO_BACKGROUND;
//            endEvent.packageName = event.getPackageName();
//
//            AppUsage appUsage = genAppUsage(startEvent, endEvent);
//            if (appUsage != null) {
//                AppUsage oldAppUsage = appUsageMap.get(appUsage.getPackageName());
//                if (oldAppUsage == null) {
//                    appUsageMap.put(appUsage.getPackageName(), appUsage);
//                } else {
//                    appUsageMap.put(appUsage.getPackageName(), AppUsage.combine(appUsage, oldAppUsage));
//                }
//            }
//        }else if (event.getEventType()==UsageEvents.Event.MOVE_TO_BACKGROUND){     //最后的事件是退出某个app，但是因为没有进入其他app的事件（正常来说会有的，但是自动熄灭屏幕没有）,所以上面那段代码不会计算该app的使用时间。
//            startEvent.timeStamp = event.getTimeStamp();
//            startEvent.eventType = -1;
//            startEvent.packageName = event.getPackageName();
//
//            endEvent.timeStamp = endTime;
//            endEvent.eventType = UsageEvents.Event.MOVE_TO_BACKGROUND;
//            endEvent.packageName = event.getPackageName();
//
//            AppUsage appUsage = genAppUsage(startEvent, endEvent);
//            if (appUsage != null) {
//                AppUsage oldAppUsage = appUsageMap.get(appUsage.getPackageName());
//                if (oldAppUsage == null) {
//                    appUsageMap.put(appUsage.getPackageName(), appUsage);
//                } else {
//                    appUsageMap.put(appUsage.getPackageName(), AppUsage.combine(appUsage, oldAppUsage));
//                }
//            }
//        }
//        if (event.getPackageName() != null) {
//            lastEventPackage = event.getPackageName();
//            lastEventType = event.getEventType();
//            spUtils.putIntValue(LAST_EVENT_TYPE, lastEventType);
//            spUtils.putStringValue(LAST_EVENT_PACKAGE, lastEventPackage);
//        } else if (!TextUtils.isEmpty(lastEventPackage) && lastEventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//            startEvent.packageName = lastEventPackage;
//
//            endEvent.packageName = lastEventPackage;
//            endEvent.timeStamp = endTime;
//            endEvent.eventType = UsageEvents.Event.MOVE_TO_BACKGROUND;
//            AppUsage appUsage = genAppUsage(startEvent, endEvent);
//            if (appUsage != null) {
//                appUsageMap.put(lastEventPackage, appUsage);
//            }
//        }
//
//        for (Map.Entry<String, AppUsage> entry : appUsageMap.entrySet()) {
//            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(entry.getKey()) + "--launch count: " + entry.getValue().getTotalLaunchCount()
//                    + " usage time: " + entry.getValue().getTotalUsageTime());
//        }
//
//        LogUtils.d(TAG, "end time: " + DateUtils.formatTime(endTime));
//
//        return appUsageMap;
//    }
//
//
//    private AppUsage genAppUsage(MyEvent startEvent, MyEvent endEvent) {
//        if (endEvent.packageName == null || !endEvent.packageName.equals(startEvent.packageName)) {
//            return null;
//        }
//        if (!isInAppPool(endEvent.packageName)) {
//            return null;
//        }
//        AppUsage appUsage = new AppUsage();
//        if (startEvent.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//            appUsage.setTotalLaunchCount(1);
//        } else {
//            appUsage.setTotalLaunchCount(0);
//        }
//        appUsage.setTotalUsageTime(endEvent.timeStamp - startEvent.timeStamp);
//        appUsage.setPackageName(endEvent.packageName);
//        appUsage.setUpdateTime(endEvent.timeStamp);
//        appUsage.setStartRecordTime(startEvent.timeStamp);
//        appUsage.setLastTimeUsed(endEvent.timeStamp);
//        return appUsage;
//    }

    private boolean isInAppPool(String packageName) {
        return userInstallApp.containsKey(packageName);
    }

    private static final class MyEvent {
        int eventType = -1;
        long timeStamp;
        String packageName;

        public MyEvent(){

        }

        public MyEvent(UsageEvents.Event event){
            timeStamp=event.getTimeStamp();
            eventType=event.getEventType();
            packageName=event.getPackageName();
        }

        public boolean isBackground(){
            return eventType== UsageEvents.Event.MOVE_TO_BACKGROUND;
        }

        public boolean isForeground(){
            return eventType== UsageEvents.Event.MOVE_TO_FOREGROUND;
        }

    }

    private static final class UsageSlice{
        String packageName;
        long usageTime;
        long startTime;
        long endTime;

        UsageSlice combine(UsageSlice right){
            if (right.packageName==null||!right.packageName.equals(packageName)){
                throw new IllegalArgumentException("package name must be same");
            }
            usageTime+=right.usageTime;
            startTime=Math.min(right.startTime,startTime);
            endTime=Math.max(right.endTime,endTime);
            return this;
        }

        AppUsage toAppUsage(){
            AppUsage appUsage=new AppUsage();
            appUsage.setPackageName(packageName);
            appUsage.setTotalUsageTime(usageTime);
            appUsage.setStartRecordTime(startTime);
            appUsage.setLastTimeUsed(endTime);
            appUsage.setUpdateTime(endTime);
            appUsage.setTotalLaunchCount(1);
            return appUsage;
        }

    }



    private Map<String, AppUsage> computeUsage(long startTime, long endTime) {
        List<UsageSlice> usageSlices=new ArrayList<>();

        MyEvent foregroundEvent=new MyEvent();
        MyEvent backgroundEvent=new MyEvent();
        MyEvent firstEvent=new MyEvent();

//        foregroundEvent.packageName=lastEventPackage;
//        foregroundEvent.timeStamp=startTime;
//        foregroundEvent.eventType=MOVE_TO_FOREGROUND;

        logDivider();

        LogUtils.d(TAG, "start time: " + DateUtils.formatTime(startTime));

        LogUtils.d(TAG, "last event package: " + AppInfoProvider.getInstance().getAppLabel(lastEventPackage));

        LogUtils.d(TAG, "last event type: " + lastEventType);


        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events = manager.queryEvents(startTime, endTime);
        UsageEvents.Event event = new UsageEvents.Event();

        logDivider();

        while (events.getNextEvent(event)){
            if (event.getEventType()== UsageEvents.Event.MOVE_TO_FOREGROUND){
                lastUpdateTime=event.getTimeStamp();
                foregroundEvent=new MyEvent(event);
                if (events.getNextEvent(event)&&
                        event.getEventType()== UsageEvents.Event.MOVE_TO_BACKGROUND){
//                    lastUpdateTime=event.getTimeStamp();
                    backgroundEvent=new MyEvent(event);
                    UsageSlice usageSlice=genUsageSlice(foregroundEvent,backgroundEvent);
                    if (usageSlice!=null){
                        usageSlices.add(usageSlice);
                    }
                    foregroundEvent.packageName=null;
                    backgroundEvent.packageName=null;
                }else if (event.getEventType()== UsageEvents.Event.MOVE_TO_FOREGROUND){
                    lastUpdateTime=event.getTimeStamp();
                }
            }
//            else if (event.getEventType()== UsageEvents.Event.MOVE_TO_BACKGROUND){
//                lastUpdateTime=event.getTimeStamp();
//            }
            LogUtils.d(TAG,"class: "+event);
            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(event.getPackageName()) + " -- event type: " + event.getEventType() + " time:" + DateUtils.formatTime(event.getTimeStamp()));
        }

        if (event.getEventType()== UsageEvents.Event.MOVE_TO_BACKGROUND){
            lastUpdateTime=endTime;
        }


//        while (events.getNextEvent(event)){
//            if (TextUtils.isEmpty(firstEvent.packageName)){
//                firstEvent.packageName=event.getPackageName();
//                firstEvent.timeStamp=event.getTimeStamp();
//                firstEvent.eventType=event.getEventType();
//            }
//            if (event.getEventType()== UsageEvents.Event.MOVE_TO_FOREGROUND){
//                foregroundEvent=new MyEvent(event);
//            }else if (event.getEventType()== UsageEvents.Event.MOVE_TO_BACKGROUND){
//                lastUpdateTime=event.getTimeStamp();
//                backgroundEvent=new MyEvent(event);
////                if (backgroundEvent.packageName.equals(foregroundEvent.packageName)){
////                    UsageSlice usageSlice=genUsageSlice(foregroundEvent,backgroundEvent);
////                    usageSlices.add(usageSlice);
////                    foregroundEvent.packageName=null;
////                    backgroundEvent.packageName=null;
////                }
//                UsageSlice usageSlice=genUsageSlice(foregroundEvent,backgroundEvent);
//                if (usageSlice!=null){
//                    usageSlices.add(usageSlice);
//                }
//                foregroundEvent.packageName=null;
//                backgroundEvent.packageName=null;
//            }
//
//            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(event.getPackageName()) + " -- event type: " + event.getEventType() + " time:" + DateUtils.formatTime(event.getTimeStamp()));
//        }

        // 减去下面那段可能多计算的代码
//        if (lastEventType== UsageEvents.Event.MOVE_TO_FOREGROUND
//                &&firstEvent.eventType!=UsageEvents.Event.MOVE_TO_BACKGROUND
//                &&!lastEventPackage.equals(firstEvent.packageName)){
//            UsageSlice usageSlice=new UsageSlice();
//            usageSlice.packageName=lastEventPackage;
//            usageSlice.startTime=startTime;
//            usageSlice.endTime=endTime;
//            usageSlice.usageTime=lastEventTimeStamp-startTime;
//            usageSlices.add(usageSlice);
//        }

        logDivider();

//        if (event.getEventType()== UsageEvents.Event.MOVE_TO_FOREGROUND){  // 最后一个事件是foreground，证明到更新为止一直在此app中
//            backgroundEvent.packageName=event.getPackageName();
//            backgroundEvent.timeStamp=endTime;
//            backgroundEvent.eventType=MOVE_TO_BACKGROUND;
//            UsageSlice usageSlice=genUsageSlice(foregroundEvent,backgroundEvent);
//            usageSlices.add(usageSlice);
//        }


//        if (event.getPackageName()!=null){
//            lastEventPackage=event.getPackageName();
//            lastEventType=event.getEventType();
//            lastEventTimeStamp=event.getTimeStamp();
//            spUtils.putStringValue(LAST_EVENT_PACKAGE,lastEventPackage);
//            spUtils.putIntValue(LAST_EVENT_TYPE,lastEventType);
//            spUtils.putLongValue(LAST_EVENT_TIME_STAMP,lastEventTimeStamp);
//        }else if (lastEventType== UsageEvents.Event.MOVE_TO_FOREGROUND){   //更新时间段内没有其他事件，且上个事件是foreground，证明一直在此app中
//            UsageSlice usageSlice=new UsageSlice();   // 有一种情况是用户没有点击，但是有 foreground 事件（应用迅速打开关闭一个画面），这时候这段代码很容易导致错误计时。
//            usageSlice.packageName=lastEventPackage;
//            usageSlice.startTime=startTime;
//            usageSlice.endTime=endTime;
//            usageSlice.usageTime=endTime-startTime;
//            usageSlices.add(usageSlice);
//        }

        usageSlices=combineUsageSlice(usageSlices);  // 合并相邻的 foreground 和 background
        Map<String, AppUsage> appUsageMap=genAppUsages(usageSlices);

        LogUtils.d(TAG, "end time: " + DateUtils.formatTime(endTime));

        logDivider();

        spUtils.putLongValue(LAST_UPDATE_TIME,lastUpdateTime);

        return appUsageMap;
    }

    private UsageSlice genUsageSlice(MyEvent foregroundEvent,MyEvent backgroundEvent){
        if (foregroundEvent==null||foregroundEvent.packageName==null
                ||backgroundEvent==null||backgroundEvent.packageName==null
                ||!backgroundEvent.packageName.equals(foregroundEvent.packageName)){
            return null;
        }
        UsageSlice slice=new UsageSlice();
        slice.packageName=backgroundEvent.packageName;
        slice.usageTime=backgroundEvent.timeStamp-foregroundEvent.timeStamp;
        slice.startTime=foregroundEvent.timeStamp;
        slice.endTime=backgroundEvent.timeStamp;
        return slice;
    }

    private List<UsageSlice> combineUsageSlice(List<UsageSlice> usageSlices){
        filterUsageSlice(usageSlices);

        for (UsageSlice slice:usageSlices){
            LogUtils.d(TAG,"slice: "+AppInfoProvider.getInstance().getAppLabel(slice.packageName));
        }

        if (usageSlices==null||usageSlices.size()==0){
            return usageSlices;
        }
        List<UsageSlice> result=new ArrayList<>();
        UsageSlice lastSlice=usageSlices.get(0);
        UsageSlice currentSlice;
        for (int i=1;i<usageSlices.size();++i){
            currentSlice=usageSlices.get(i);
            if (lastSlice.packageName.equals(currentSlice.packageName)){
                lastSlice.combine(currentSlice);
            }else {
                result.add(lastSlice);
                lastSlice=currentSlice;
            }
        }
        result.add(lastSlice);

        logDivider();

        for (UsageSlice slice:result){
            LogUtils.d(TAG,"slice: "+AppInfoProvider.getInstance().getAppLabel(slice.packageName));
        }

        return result;
    }

    private void filterUsageSlice(List<UsageSlice> usageSlices){
        for (Iterator<UsageSlice> iterator=usageSlices.iterator();iterator.hasNext();){
            UsageSlice usageSlice=iterator.next();
            if (Math.abs(usageSlice.usageTime)<=200){    // 某个页面的停留时间小于200ms，一般不是用户手动点击打开的。腾讯新闻会出现这种情况，明明没有手动点击却会有 移动到前台 的事件。
                iterator.remove();
            }
        }
    }

    private Map<String,AppUsage> genAppUsages(List<UsageSlice> usageSlices){
        Map<String,AppUsage> appUsages=new ArrayMap<>();
        if (usageSlices==null||usageSlices.size()==0){
            return appUsages;
        }
        AppUsage appUsage;
        AppUsage oldAppUsage;

        appUsage=usageSlices.get(0).toAppUsage();
        if (appUsage.getPackageName().equals(lastEventPackage)){
            appUsage.setTotalLaunchCount(appUsage.getTotalLaunchCount()-1);
        }

        appUsages.put(appUsage.getPackageName(),appUsage);

        for (int i=1;i<usageSlices.size();++i){
            appUsage=usageSlices.get(i).toAppUsage();
            oldAppUsage=appUsages.get(appUsage.getPackageName());
            if (oldAppUsage==null){
                appUsages.put(appUsage.getPackageName(),appUsage);
            }else {
                appUsages.put(appUsage.getPackageName(),AppUsage.combine(appUsage,oldAppUsage));
            }
        }

        logDivider();

        for (Map.Entry<String, AppUsage> entry : appUsages.entrySet()) {
            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(entry.getKey()) + "--launch count: " + entry.getValue().getTotalLaunchCount()
                    + " usage time: " + entry.getValue().getTotalUsageTime());
        }
        logDivider();

        return appUsages;
    }

    private void logDivider(){
        LogUtils.d(TAG,"-------------");
    }

}
