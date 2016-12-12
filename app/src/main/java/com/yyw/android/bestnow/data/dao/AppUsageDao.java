package com.yyw.android.bestnow.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.yyw.android.bestnow.data.dao.AppUsage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP_USAGE".
*/
public class AppUsageDao extends AbstractDao<AppUsage, String> {

    public static final String TABLENAME = "APP_USAGE";

    /**
     * Properties of entity AppUsage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property PackageName = new Property(0, String.class, "packageName", true, "PACKAGE_NAME");
        public final static Property Label = new Property(1, String.class, "label", false, "LABEL");
        public final static Property TotalLaunchCount = new Property(2, Integer.class, "totalLaunchCount", false, "TOTAL_LAUNCH_COUNT");
        public final static Property TotalUsageTime = new Property(3, Long.class, "totalUsageTime", false, "TOTAL_USAGE_TIME");
        public final static Property LastTimeUsed = new Property(4, Long.class, "lastTimeUsed", false, "LAST_TIME_USED");
        public final static Property StartRecordTime = new Property(5, Long.class, "startRecordTime", false, "START_RECORD_TIME");
        public final static Property UpdateTime = new Property(6, Long.class, "updateTime", false, "UPDATE_TIME");
    };


    public AppUsageDao(DaoConfig config) {
        super(config);
    }
    
    public AppUsageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP_USAGE\" (" + //
                "\"PACKAGE_NAME\" TEXT PRIMARY KEY NOT NULL ," + // 0: packageName
                "\"LABEL\" TEXT," + // 1: label
                "\"TOTAL_LAUNCH_COUNT\" INTEGER," + // 2: totalLaunchCount
                "\"TOTAL_USAGE_TIME\" INTEGER," + // 3: totalUsageTime
                "\"LAST_TIME_USED\" INTEGER," + // 4: lastTimeUsed
                "\"START_RECORD_TIME\" INTEGER," + // 5: startRecordTime
                "\"UPDATE_TIME\" INTEGER);"); // 6: updateTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP_USAGE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AppUsage entity) {
        stmt.clearBindings();
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(1, packageName);
        }
 
        String label = entity.getLabel();
        if (label != null) {
            stmt.bindString(2, label);
        }
 
        Integer totalLaunchCount = entity.getTotalLaunchCount();
        if (totalLaunchCount != null) {
            stmt.bindLong(3, totalLaunchCount);
        }
 
        Long totalUsageTime = entity.getTotalUsageTime();
        if (totalUsageTime != null) {
            stmt.bindLong(4, totalUsageTime);
        }
 
        Long lastTimeUsed = entity.getLastTimeUsed();
        if (lastTimeUsed != null) {
            stmt.bindLong(5, lastTimeUsed);
        }
 
        Long startRecordTime = entity.getStartRecordTime();
        if (startRecordTime != null) {
            stmt.bindLong(6, startRecordTime);
        }
 
        Long updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(7, updateTime);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AppUsage readEntity(Cursor cursor, int offset) {
        AppUsage entity = new AppUsage( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // packageName
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // label
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // totalLaunchCount
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // totalUsageTime
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // lastTimeUsed
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // startRecordTime
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // updateTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AppUsage entity, int offset) {
        entity.setPackageName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLabel(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTotalLaunchCount(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setTotalUsageTime(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setLastTimeUsed(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setStartRecordTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setUpdateTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(AppUsage entity, long rowId) {
        return entity.getPackageName();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(AppUsage entity) {
        if(entity != null) {
            return entity.getPackageName();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
