package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Iterator;
import java.util.List;

/**
 * Created by agrizzi-californium on 28/11/15.
 */

public class SensorController extends Controller {

    public static ObjectNode CreateError(String message){
        ObjectNode resp = Json.newObject();
        resp.put("error",message);
        return resp;
    }

    public static Result registerSensor() {
        JsonNode json = request().body().asJson();
        if(!json.has("label")) return badRequest(CreateError("missing label")).as("application/json");
        if(!json.has("resource")) return badRequest(CreateError("missing resource")).as("application/json");

        if(json.findPath("label").textValue().isEmpty()) return badRequest(CreateError("missing label")).as("application/json");
        if(json.findPath("resource").textValue().isEmpty()) return badRequest(CreateError("missing resource")).as("application/json");

        Sensor sensor = new Sensor(json.findPath("label").textValue());
        Resource resource = new Resource(json.findPath("resource").textValue());
        sensor.getResources().add(resource);
        resource.setSensor(sensor);
        sensor.save();
        resource.save();
        return ok(sensor.toJson()).as("application/json");
    }

    public static Result getSensorList(){
        ArrayNode listOut = JsonNodeFactory.instance.arrayNode();
        List<Sensor> listSensor = Sensor.find.all();
        if(listSensor.isEmpty()) return badRequest(CreateError("sensor not found")).as("application/json");
        Iterator<Sensor> it = listSensor.iterator();
        while (it.hasNext()) {
            listOut.add(it.next().toJson());
        }
        return ok(listOut).as("application/json");
    }

    public static Result getSensor(String sensorToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN",sensorToken).findUnique();
        if(sensor == null) return badRequest(CreateError("sensor not found")).as("application/json");
        ObjectNode json = sensor.toJson();
        ArrayNode listArrayNode = json.putArray("resources");
        List<Resource> listResource = Resource.find.all();
            if(listResource.isEmpty()) return badRequest(CreateError("resources not found")).as("application/json");
            Iterator<Resource> it = listResource.iterator();
            while (it.hasNext()) {
                listArrayNode.add(it.next().toJson());
            }
        return ok(json).as("application/json");
    }

    public static Result getResource(String sensorToken,String resourceToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN", sensorToken).findUnique();
        if(sensor == null) return badRequest(CreateError("sensor not found")).as("application/json");
        ArrayNode json = JsonNodeFactory.instance.arrayNode();
        Resource resource = Resource.find.where().ilike("RESOURCE_TOKEN", resourceToken).findUnique();
        if(resource == null) return badRequest(CreateError("resource not found")).as("application/json");
        Iterator<ResourceData> it = resource.getData().iterator();
        while (it.hasNext()) {
            json.add(it.next().toJson());
        }
        return ok(json).as("application/json");
    }
}
