package com.yyw.android.bestnow.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "APP".
 */
public class AppDao extends AbstractDao<App, String> {

    public static final String TABLENAME = "APP";

    /**
     * Properties of entity App.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PackageName = new Property(0, String.class, "packageName", true, "PACKAGE_NAME");
        public final static Property Label = new Property(1, String.class, "label", false, "LABEL");
        public final static Property ShouldStatistic = new Property(2, Boolean.class, "shouldStatistic", false, "SHOULD_STATISTIC");
        public final static Property IsLimit = new Property(3, Boolean.class, "isLimit", false, "IS_LIMIT");
        public final static Property LimitTime = new Property(4, Long.class, "limitTime", false, "LIMIT_TIME");
    }

    ;


    public AppDao(DaoConfig config) {
        super(config);
    }

    public AppDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP\" (" + //
                "\"PACKAGE_NAME\" TEXT PRIMARY KEY NOT NULL ," + // 0: packageName
                "\"LABEL\" TEXT," + // 1: label
                "\"SHOULD_STATISTIC\" INTEGER," + // 2: shouldStatistic
                "\"IS_LIMIT\" INTEGER," + // 3: isLimit
                "\"LIMIT_TIME\" INTEGER);"); // 4: limitTime
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, App entity) {
        stmt.clearBindings();

        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(1, packageName);
        }

        String label = entity.getLabel();
        if (label != null) {
            stmt.bindString(2, label);
        }

        Boolean shouldStatistic = entity.getShouldStatistic();
        if (shouldStatistic != null) {
            stmt.bindLong(3, shouldStatistic ? 1L : 0L);
        }

        Boolean isLimit = entity.getIsLimit();
        if (isLimit != null) {
            stmt.bindLong(4, isLimit ? 1L : 0L);
        }

        Long limitTime = entity.getLimitTime();
        if (limitTime != null) {
            stmt.bindLong(5, limitTime);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public App readEntity(Cursor cursor, int offset) {
        App entity = new App( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // packageName
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // label
                cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // shouldStatistic
                cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // isLimit
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // limitTime
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, App entity, int offset) {
        entity.setPackageName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLabel(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setShouldStatistic(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setIsLimit(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setLimitTime(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(App entity, long rowId) {
        return entity.getPackageName();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(App entity) {
        if (entity != null) {
            return entity.getPackageName();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
