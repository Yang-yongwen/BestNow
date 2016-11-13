package com.yyw.android;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGeneratorBestNow {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.yyw.android.bestnow.data.dao");
        addAppUsage(schema);
        addPerHourUsage(schema);
        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    private static void addAppUsage(Schema schema) {
        Entity appUsage = schema.addEntity("AppUsage");

//        appUsage.addIntProperty("baseLaunchCount");
//        appUsage.addLongProperty("baseUsageTime");
//        appUsage.addLongProperty("baseTime");
//        appUsage.addIntProperty("launchCountSinceBase");
        appUsage.addStringProperty("packageName").primaryKey();
        appUsage.addStringProperty("label");

        appUsage.addLongProperty("UsageTimeSinceBase");

        appUsage.addIntProperty("totalLaunchCount");
        appUsage.addLongProperty("totalUsageTime");

        appUsage.addLongProperty("lastTimeUsed");
        appUsage.addLongProperty("startRecordTime");
        appUsage.addLongProperty("updateTime");
    }

    private static void addPerHourUsage(Schema schema){
        Entity perHourUsage=schema.addEntity("PerHourUsage");
        perHourUsage.addIdProperty();
        perHourUsage.addStringProperty("packageName");
        perHourUsage.addLongProperty("time");
        perHourUsage.addIntProperty("launchCount");
        perHourUsage.addLongProperty("usageTime");
    }

}