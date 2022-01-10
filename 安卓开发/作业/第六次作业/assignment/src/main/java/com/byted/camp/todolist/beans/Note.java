package com.byted.camp.todolist.beans;

import java.util.Date;

/**
 * @author zhongshan
 * @date 2021-04-19
 */
public class Note {

    public final long id;
    private String date;
    private State state;
    private String content;

    // todo优先级
    private int priority;

    public Note(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
