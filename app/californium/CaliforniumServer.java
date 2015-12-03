/*******************************************************************************
 * Copyright (c) 2014 Institute for Pervasive Computing, ETH Zurich and others.
 * Modifications (c) 2015 Watr.li
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/

package californium;

import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Resource;
import models.ResourceData;
import models.Sensor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import play.Logger;

import actors.messages.*;

import akka.actor.*;
import static akka.pattern.Patterns.ask;

/**
 * Eclipse Californium server that handles all received CoAP messages and forwards them
 * to the Play application.
 */
public class CaliforniumServer extends CoapServer {

    final static Logger.ALogger logger = Logger.of("CaliforniumServer");

    // Reference to the actor that will be notified if a CoAP message is received.
    ActorRef serverActor;

    public static CaliforniumServer initialize(ActorRef serverActor) {
        CaliforniumServer server = null;

        try {
            server = new CaliforniumServer(serverActor);
            server.start();
            logger.info("Server initialized");

        } catch (SocketException e) {
            logger.error("Failed to initialize server: " + e.getMessage());
        }

        return server;
    }

    /*
     * The CoAP resources of the server are initialized.
     */
    public CaliforniumServer(ActorRef serverActor) throws SocketException {
        this.serverActor = serverActor;

        // provide an instance of a Hello-World resource
        add(new SensorResource());
    }

    class SensorResource extends CoapResource {

        public SensorResource() {
            // set resource identifier
            super("sensor");
            getAttributes().setTitle("Sensor Resource");
        }

        @Override
        public void handlePOST(CoapExchange exchange) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                serverActor.tell(new CoapMessageReceived(exchange.getRequestText()), null);
                JsonNode json = mapper.readTree(exchange.getRequestText());
                //////
                readJSON(json,exchange);
                //////
                Response response = new Response(CoAP.ResponseCode.CREATED);
                response.setPayload(json.toString());
                exchange.respond(response);

            }catch (JsonProcessingException e){
                exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
            }catch (IOException e){
                exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
            }
        }

        public void readJSON(JsonNode json, CoapExchange exchange){
            String sensorToken;
            String resourceToken;
            double value;
            long timeStamp;

            JsonNode resourceNode = json.path("resource");
            JsonNode dataNode = resourceNode.path("data");

            sensorToken = json.path("sensorToken").asText();
            Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN",sensorToken).findUnique();
            List<Resource> listResource = sensor.getResources();

            if (resourceNode.isMissingNode()) {
                //TODO nó do JSON não contem resource
                exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
            } else {
                resourceToken = resourceNode.path("resourceToken").asText();
                Iterator<Resource> it = listResource.iterator();
                while(it.hasNext()){
                    Resource current = it.next();
                    if(current.getResourceToken().equals(resourceToken)){
                        if (!dataNode.isArray()) {
                            //TODO erro no formato JSON, deveria ser um Array
                            exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
                        } else {
                            for (JsonNode node : dataNode) {
                                timeStamp = node.path("timestamp").asLong();
                                value = node.path("value").asDouble();
                                ResourceData data = new ResourceData(value,timeStamp);
                                current.getData().add(data);
                                current.save();
                                data.save();
                                break;
                            }
                        }
                    } else {
                        //TODO não existe o resource para esse sensor
                        exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
                    }

                }

            }
        }
    }
}
