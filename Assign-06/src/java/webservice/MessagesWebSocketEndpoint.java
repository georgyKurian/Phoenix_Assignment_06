/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import controller.Messages;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 *
 * @author Georgi
 */
@ServerEndpoint("/socket")
@ApplicationScoped
public class MessagesWebSocketEndpoint {

    @Inject
    Messages messages;
    
    List<Session> sessionList = new ArrayList<>();
    
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        String output = "";

        // Reading the json received
        JsonObject json = Json.createReader(new StringReader(message)).readObject();
            
        if(!sessionList.contains(session)){
            sessionList.add(session);
        }
        
        // Generating iutput json
        if (json.containsKey("getAll") && json.getBoolean("getAll") == true) {

            output = messages.getJsonMessages().toString();

        } else if (json.containsKey("getById")) {
            output = messages.getMessageById(json.getInt("getById")).getJson().toString();
        } else if (json.containsKey("getFromTo")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                JsonArray dateArray = json.getJsonArray("getFromTo");

                String fromDate = dateArray.getString(0);
                String toDate = dateArray.getString(1);

                output = messages.getMessagesByDate(sdf.parse(fromDate), sdf.parse(toDate)).toString();
            } catch (ParseException ex) {
                Logger.getLogger(MessagesREST.class.getName()).log(Level.SEVERE, null, ex);
                output = "{'error':'Date parsing error'}";
            }
        } 
        else if (json.containsKey("put")) {
            JsonObject putData = json.getJsonObject("put");
            if (putData.containsKey("id")) {
                if(messages.updateMessageById(putData.getInt("id"), putData)!= null){
                    output = "{'ok': true }";
                }
                else{
                    output = "{'error':'Update error'}";
                }
            }else{
                output = "{'error':'Missing id'}";
            }
        } else if (json.containsKey("delete")) {

            if (messages.deleteMessageById(json.getInt("delete"))) {
                output = "{'ok': true }";
            }else{
                output = "{'error':'Invalid id'}";
            }

        }
        // Sending the data back
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        basic.sendText(output);
        
        // Sending data when to all clients when received post request
        if (json.containsKey("post")) {
            JsonObject postData = json.getJsonObject("post");
            output = messages.insertMessage(postData).toString();
            for (Session s : sessionList) {
                RemoteEndpoint.Basic client = s.getBasicRemote();
                client.sendText(output);
            }
        } 

    }

}
