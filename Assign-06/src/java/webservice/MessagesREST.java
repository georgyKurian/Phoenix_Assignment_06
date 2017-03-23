/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservice;

import controller.Messages;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author c0687174
 */
@Path("messages")
@ApplicationScoped
public class MessagesREST {

    @Inject
    private Messages messages;

    /**
     * Retrieves all the messages in Json
     *
     * @return Array of Json objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getJson() {
        return messages.getJsonMessages();
    }

    /**
     * Retrieves the json with the particular id
     *
     * @param id Id of the message
     * @return json of the message
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public JsonObject getJsonById(@PathParam("id") int id) {
        return messages.getMessageById(id).getJson();
    }

    /**
     * Retrieves all the messages between the dates
     *
     * @param fromDate from date of the mesages
     * @param toDate to date of he messages
     * @return Array of Json messages
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{fromDate}/{toDate}")
    public JsonArray getJsonByDate(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            return messages.getMessagesByDate(sdf.parse(fromDate), sdf.parse(toDate));
        } catch (ParseException ex) {
            Logger.getLogger(MessagesREST.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Inserts the message send
     *
     * @param content Json from the client
     * @return Inserted Json with id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject postJson(String content) {
        JsonObject json = Json.createReader(new StringReader(content)).readObject();
        return messages.insertMessage(json);
    }

    /**
     * Updates a particular message details
     *
     * @param content Json with uodated details
     * @param id Id of the message
     * @return Updated Json of the message
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public JsonObject putJson(String content, @PathParam("id") int id) {

        JsonObject json = Json.createReader(new StringReader(content)).readObject();

        return messages.updateMessageById(id, json).getJson();

    }

    /**
     * Deletes the message with the Id
     *
     * @param id The id of the message
     * @return response code
     */
    @DELETE
    @Path("{id}")
    public Response deleteMessage(@PathParam("id") int id) {
        if (messages.deleteMessageById(id)) {
            return Response.status(200).entity(id).build();
        } else {
            return Response.status(401).build();
        }

    }
}
