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
        Sensor sensor = new Sensor(json.findPath("label").textValue());
        Resource resource = new Resource(json.findPath("resource").textValue());
        sensor.getResources().add(resource);
        resource.setSensor(sensor);
        sensor.save();
        resource.save();
        return ok(sensor.toJson());
    }

    public static Result getSensorList(){
        ArrayNode listOut = JsonNodeFactory.instance.arrayNode();
        List<Sensor> listSensor = Sensor.find.all();
        Iterator<Sensor> it = listSensor.iterator();
        while(it.hasNext()){
            listOut.add(it.next().toJson());
        }
        return ok(listOut);
    }
    public static Result getSensor(String sensorToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN",sensorToken).findUnique();
        ObjectNode json = sensor.toJson();
        ArrayNode listArrayNode = json.putArray("resources");
        List<Resource> listResource = Resource.find.all();
        Iterator<Resource> it = listResource.iterator();
        while(it.hasNext()){
            listArrayNode.add(it.next().toJson());
        }
        return ok(json);
    }
    public static Result getResource(String sensorToken,String resourceToken){
        Sensor sensor = Sensor.find.where().ilike("SENSOR_TOKEN", sensorToken).findUnique();
        ArrayNode json = JsonNodeFactory.instance.arrayNode();
        Resource resource = Resource.find.where().ilike("RESOURCE_TOKEN",resourceToken).findUnique();

        Iterator<ResourceData> it = resource.getData().iterator();
        while(it.hasNext()){
            json.add(it.next().toJson());
        }
        return ok(json);
    }
}
