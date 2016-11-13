package com.yyw.android.bestnow.common.utils;

import android.os.Environment;
import android.util.Log;

import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by samsung on 2016/10/28.
 */

public class LogUtils {
    private static final String LOG_TAG = "AppStatistic";
    private static final String LOG_PREFIX = "AppStatistic_";
    private static final String LOG_FILE_DIR = "AppStatistic_LOG";
    private static final int LOG_METHOD_COUNT = 2;
    private static final int LOG_METHOD_OFFSET = 1;
    private static final int LOG_FILE_LIMIT = 1024 * 1024 * 5;
    private static final int LOG_FILE_MAX_COUNT = 5;
    private static int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static int MAX_LOG_TAG_LENGTH = 30;
    public static boolean LOGGING_ENABLED = true;
    public static boolean LOGGING_TO_FILE = true;

    public static File logFileDir;
    private static LogUtils instance;
    private java.util.logging.Logger nativeLogger;
    private Date date;
    private SimpleDateFormat formatter;

    private LogAdapter customLogAdapter = new LogAdapter() {
        @Override
        public void d(String tag, String message) {
            Log.d(tag, message);
            message = buildFileLogMessage("D", message);
            nativeLogger.info(message);
        }

        @Override
        public void e(String tag, String message) {
            Log.e(tag, message);
            message = buildFileLogMessage("E", message);
            nativeLogger.severe(message);
        }

        @Override
        public void w(String tag, String message) {
            Log.w(tag, message);
            message = buildFileLogMessage("W", message);
            nativeLogger.warning(message);
        }

        @Override
        public void i(String tag, String message) {
            Log.i(tag, message);
            message = buildFileLogMessage("I", message);
            nativeLogger.info(message);
        }

        @Override
        public void v(String tag, String message) {
            Log.v(tag, message);
            message = buildFileLogMessage("V", message);
            nativeLogger.info(message);
        }

        @Override
        public void wtf(String tag, String message) {
            Log.wtf(tag, message);
            message = buildFileLogMessage("WTF", message);
            nativeLogger.info(message);
        }

        String buildFileLogMessage(String type, String message) {
            date.setTime(System.currentTimeMillis());
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(type).append("] ");
            builder.append(formatter.format(date)).append(" ");
            builder.append(message);
            return builder.toString();
        }
    };

    private LogUtils() {
        nativeLogger = java.util.logging.Logger.getLogger(LOG_TAG);
        if (nativeLogger != null) {
            nativeLogger.setLevel(Level.ALL);
            nativeLogger.setUseParentHandlers(false);
        }
        formatter = new SimpleDateFormat("MM/dd HH:mm:ss.SSS", Locale.getDefault());
        date = new Date();
    }

    private static LogUtils getInstance() {
        if (instance == null) {
            instance = new LogUtils();
        }
        return instance;
    }

    public static void init() {
        LogUtils logUtils = getInstance();
        if (LOGGING_TO_FILE) {
            try {
                logFileDir = new File(Environment.getExternalStorageDirectory(), LOG_FILE_DIR);
                if (!logFileDir.exists()) {
                    logFileDir.mkdir();
                }

                StringBuilder pathBuilder = new StringBuilder(logFileDir.getPath());
                pathBuilder.append(File.separator);
                pathBuilder.append(LOG_TAG);
                pathBuilder.append("%g%u.txt");

                FileHandler fileHandler = new FileHandler(pathBuilder.toString(), LOG_FILE_LIMIT, LOG_FILE_MAX_COUNT, true);
                fileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord r) {
                        return r.getMessage() + "\n";
                    }
                });
                logUtils.nativeLogger.addHandler(fileHandler);
                Logger.init().methodOffset(LOG_METHOD_OFFSET).methodCount(LOG_METHOD_COUNT).logAdapter(logUtils.customLogAdapter);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.init().methodOffset(LOG_METHOD_OFFSET).methodCount(LOG_METHOD_COUNT);
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void v(final String tag, String msg) {
        if (LOGGING_ENABLED) {
            Logger.t(tag).v(msg);
        }
    }

    public static void i(final String tag, String msg) {
        if (LOGGING_ENABLED) {
            Logger.t(tag).i(msg);
        }
    }

    public static void w(final String tag, String msg) {
        if (LOGGING_ENABLED) {
            Logger.t(tag).w(msg);
        }
    }

    public static void e(final String tag, String msg) {
        if (LOGGING_ENABLED) {
            Logger.t(tag).e(msg);
        }
    }

    public static void d(final String tag, String msg) {
        if (LOGGING_ENABLED) {
            Logger.t(tag).d(msg);
        }
    }
}
