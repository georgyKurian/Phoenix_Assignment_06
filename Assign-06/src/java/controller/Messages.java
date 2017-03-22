/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import model.Message;

/**
 *
 * @author c0687174
 */
@ApplicationScoped
public class Messages {

    private List<Message> messageList;
    private int index;

    public Messages() {
        this.messageList = new ArrayList<>();
        index = 0;
    }

    public Message getMessageById(int id) {

        for (Message message : messageList) {
            if (message.getId() == id) {
                return message;
            }
        }
        return null;
    }

    public Message updateMessageById(int id, JsonObject obj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        for (Message message : messageList) {
            if (message.getId() == id) {
                message.setAuthor(obj.getString("author"));
                message.setContents(obj.getString("contents"));
                message.setTitle(obj.getString("title"));
                try {
                    message.setSentTime(sdf.parse(obj.getString("sentTime")));
                } catch (ParseException ex) {
                    Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
                    message.setSentTime(new Date());
                }
                return message;
            }
        }
        return null;
    }

    public boolean deleteMessageById(int id) {

        for (Message message : messageList) {
            if (message.getId() == id) {
                messageList.remove(message);
                return true;
            }
        }
        return false;
    }

    public JsonArray getMessagesByDate(Date fromDate, Date toDate) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Message message : messageList) {
            if (!(message.getSentTime().before(fromDate) || message.getSentTime().after(toDate))) {
                jsonArrayBuilder.add(message.getJson());
            }
        }
        return jsonArrayBuilder.build();
    }

    public JsonArray getJsonMessages() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Message message : messageList) {
            jsonArrayBuilder.add(message.getJson());
        }
        return jsonArrayBuilder.build();
    }

    public JsonObject insertMessage(JsonObject json) {
        Message msg = new Message(index, json);
        messageList.add(msg);
        index++;
        return msg.getJson();

    }

}
