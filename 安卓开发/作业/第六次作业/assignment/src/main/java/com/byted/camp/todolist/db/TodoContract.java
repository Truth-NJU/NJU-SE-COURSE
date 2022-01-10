package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

import java.util.Date;
import java.util.stream.BaseStream;

/**
 * @author zhongshan
 * @date 2021-04-19
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    // 建表语句
    public static final String SQL_CREATE_TODO=
            "CREATE TABLE " + todoList.TABLE_NAME + " (" +
                    todoList._ID + " INTEGER PRIMARY KEY," +
                    todoList.DATE + " DATE," +
                    todoList.STATE + " INTEGER,"+
                    todoList.CONTENT+" VARCHAR,"+
                    todoList.PRIORITY+" INTEGER)";


    public static final String SQL_DELETE_TODO = "DROP TABLE IF EXISTS " + todoList.TABLE_NAME;

    private TodoContract() {
    }

    public static class todoList implements BaseColumns{
        //定义sql语句常量
        public static final String TABLE_NAME="todoList";
        public static final String DATE="date";
        public static final String STATE="state";
        public static final String CONTENT="content";
        public static final String PRIORITY="priority";
    }

}
