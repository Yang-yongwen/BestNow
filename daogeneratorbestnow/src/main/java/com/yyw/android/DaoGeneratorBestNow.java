package com.yyw.android;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGeneratorBestNow {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.yyw.android.bestnow.data.dao");
        addAppUsage(schema);
        addPerHourUsage(schema);
        addApp(schema);
        addEvent(schema);
        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    private static void addAppUsage(Schema schema) {
        Entity appUsage = schema.addEntity("AppUsage");

        appUsage.addStringProperty("packageName").primaryKey();
        appUsage.addStringProperty("label");
        appUsage.addIntProperty("totalLaunchCount");
        appUsage.addLongProperty("totalUsageTime");
        appUsage.addLongProperty("lastTimeUsed");
        appUsage.addLongProperty("startRecordTime");
        appUsage.addLongProperty("updateTime");
        appUsage.setHasKeepSections(true);
    }

    private static void addPerHourUsage(Schema schema){
        Entity perHourUsage=schema.addEntity("PerHourUsage");
        perHourUsage.addIdProperty();
        perHourUsage.addStringProperty("packageName");
        perHourUsage.addLongProperty("time");
        perHourUsage.addStringProperty("formatTime");
        perHourUsage.addIntProperty("launchCount");
        perHourUsage.addLongProperty("usageTime");
        perHourUsage.addStringProperty("date");
        perHourUsage.addIntProperty("hour");
    }

    private static void addApp(Schema schema){
        Entity app=schema.addEntity("App");
        app.addStringProperty("packageName").primaryKey();
        app.addStringProperty("label");
        app.addBooleanProperty("shouldStatistic");
        app.addBooleanProperty("isLimit");
        app.addLongProperty("limitTime");
    }

    private static void addEvent(Schema schema){
        Entity event=schema.addEntity("Event");
        event.addIdProperty();
        event.addStringProperty("date");
        event.addStringProperty("alarmTime");
        event.addBooleanProperty("hasAlarm");
        event.addBooleanProperty("done");
        event.addStringProperty("content");
        event.addBooleanProperty("edited");
        event.addLongProperty("createTime");
        event.setHasKeepSections(true);
    }

}
