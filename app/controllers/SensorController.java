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

    public static Result registerSensor() {
        JsonNode json = request().body().asJson();
        if(!json.has("label") && !json.has("resource")){
            ObjectNode resp = Json.newObject();
            resp.put("error","missing label or resource");
            return badRequest(resp);
        }
        if(!json.findPath("label").textValue().isEmpty() || !json.findPath("resource").textValue().isEmpty()) {
            Sensor sensor = new Sensor(json.findPath("label").textValue());
            Resource resource = new Resource(json.findPath("resource").textValue());
            sensor.getResources().add(resource);
            resource.setSensor(sensor);
            sensor.save();
            resource.save();
            return ok(sensor.toJson());
        }else{
            return badRequest("{error: 'missing label or resource'}");
        }
    }

    public static Result getSensorList(){
        ArrayNode listOut = JsonNodeFactory.instance.arrayNode();
        List<Sensor> listSensor = Sensor.find.all();
        if(!listSensor.isEmpty()) {
            Iterator<Sensor> it = listSensor.iterator();
            while (it.hasNext()) {
                listOut.add(it.next().toJson());
            }
            return ok(listOut);
        } else {
            return badRequest("{error: 'sensors not found'}");
        }
    }
    public static Result getSensor(String sensorToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN",sensorToken).findUnique();
        if(sensor != null) {
            ObjectNode json = sensor.toJson();
            ArrayNode listArrayNode = json.putArray("resources");
            List<Resource> listResource = Resource.find.all();
            if(!listResource.isEmpty()) {
                Iterator<Resource> it = listResource.iterator();
                while (it.hasNext()) {
                    listArrayNode.add(it.next().toJson());
                }
                return ok(json);
            } else {
                return badRequest("{error: 'resource not found'}");
            }
        } else {
            return badRequest("{error: 'sensor not found'}");
        }
    }
    public static Result getResource(String sensorToken,String resourceToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN", sensorToken).findUnique();
        if(sensor != null) {
            ArrayNode json = JsonNodeFactory.instance.arrayNode();
            Resource resource = Resource.find.where().ilike("RESOURCE_TOKEN", resourceToken).findUnique();
            if(resource != null) {
                Iterator<ResourceData> it = resource.getData().iterator();
                while (it.hasNext()) {
                    json.add(it.next().toJson());
                }
                return ok(json);
            } else {
                return badRequest("{error: 'resource not found'}");
            }
        } else {
            return badRequest("{error: 'sensor not found'}");
        }
    }
}
