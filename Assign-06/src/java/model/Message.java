/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author c0687174
 */
public class Message {

    private int id;
    private String title;
    private String contents;
    private String author;
    private Date sentTime;

    public Message(int id, JsonObject json) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        this.id = id;
        this.title = json.getString("title");
        this.contents = json.getString("contents");
        this.author = json.getString("author");
        try {
            this.sentTime = sdf.parse(json.getString("sentTime"));
        } catch (ParseException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
            this.sentTime = new Date();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public JsonObject getJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", id);
        json.add("title", title);
        json.add("contents", contents);
        json.add("author", author);
        json.add("sentTime", sentTime.toString());
        return json.build();
    }
}
