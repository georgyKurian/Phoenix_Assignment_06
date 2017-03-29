/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javax.json.JsonObjectBuilder;
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
        refreshData();
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
        try {
            Connection con = Database.getConnection();
            String sql = "UPDATE `message` SET `title` = ?,  `contents` = ?, `author`= ?, `sentTime` = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, obj.getString("title"));
            pstmt.setString(2, obj.getString("contents"));
            pstmt.setString(3, obj.getString("author"));
            pstmt.setString(4, obj.getString("sentTime"));
            pstmt.executeUpdate();
          
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
        } catch (SQLException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean deleteMessageById(int id) {
        try {
            Connection con = Database.getConnection();
            String sql = "DELETE FROM `message` WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            for (Message message : messageList) {
                if (message.getId() == id) {
                    messageList.remove(message);
                    return true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            Connection con = Database.getConnection();
            String sql = "INSERT INTO `message` (`id`, `title`, `contents`, `author`, `sentTime`) VALUES (NULL, ?, ?, ?,?) ";
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, json.getString("title"));
            pstmt.setString(2, json.getString("contents"));
            pstmt.setString(3, json.getString("author"));
            pstmt.setString(4, json.getString("sentTime"));
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Message msg = new Message(rs.getInt(1), json);
                messageList.add(msg);
                return msg.getJson();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void refreshData() {
        try {
            Connection con = Database.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM message");
            messageList = new ArrayList<>();
            while (rs.next()) {
                JsonObjectBuilder json = Json.createObjectBuilder();
                json.add("title", rs.getString("title"));
                json.add("author", rs.getString("author"));
                json.add("contents", rs.getString("contents"));
                json.add("sentTime", rs.getString("sentTime"));

                Message msg = new Message(rs.getInt("id"), json.build());
                messageList.add(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
