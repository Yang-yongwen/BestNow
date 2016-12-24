package com.yyw.android.bestnow.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.yyw.android.bestnow.data.dao.Event;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EVENT".
*/
public class EventDao extends AbstractDao<Event, Long> {

    public static final String TABLENAME = "EVENT";

    /**
     * Properties of entity Event.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Date = new Property(1, String.class, "date", false, "DATE");
        public final static Property AlarmTime = new Property(2, String.class, "alarmTime", false, "ALARM_TIME");
        public final static Property HasAlarm = new Property(3, Boolean.class, "hasAlarm", false, "HAS_ALARM");
        public final static Property Done = new Property(4, Boolean.class, "done", false, "DONE");
        public final static Property Content = new Property(5, String.class, "content", false, "CONTENT");
        public final static Property Edited = new Property(6, Boolean.class, "edited", false, "EDITED");
        public final static Property CreateTime = new Property(7, Long.class, "createTime", false, "CREATE_TIME");
    };


    public EventDao(DaoConfig config) {
        super(config);
    }
    
    public EventDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EVENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DATE\" TEXT," + // 1: date
                "\"ALARM_TIME\" TEXT," + // 2: alarmTime
                "\"HAS_ALARM\" INTEGER," + // 3: hasAlarm
                "\"DONE\" INTEGER," + // 4: done
                "\"CONTENT\" TEXT," + // 5: content
                "\"EDITED\" INTEGER," + // 6: edited
                "\"CREATE_TIME\" INTEGER);"); // 7: createTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EVENT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Event entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
 
        String alarmTime = entity.getAlarmTime();
        if (alarmTime != null) {
            stmt.bindString(3, alarmTime);
        }
 
        Boolean hasAlarm = entity.getHasAlarm();
        if (hasAlarm != null) {
            stmt.bindLong(4, hasAlarm ? 1L: 0L);
        }
 
        Boolean done = entity.getDone();
        if (done != null) {
            stmt.bindLong(5, done ? 1L: 0L);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(6, content);
        }
 
        Boolean edited = entity.getEdited();
        if (edited != null) {
            stmt.bindLong(7, edited ? 1L: 0L);
        }
 
        Long createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(8, createTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Event readEntity(Cursor cursor, int offset) {
        Event entity = new Event( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // date
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // alarmTime
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // hasAlarm
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // done
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // content
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // edited
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // createTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Event entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAlarmTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setHasAlarm(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setDone(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setContent(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setEdited(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setCreateTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Event entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Event entity) {
        if(entity != null) {
            return entity.getId();
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