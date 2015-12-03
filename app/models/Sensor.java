package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by agrizzi-californium on 28/11/15.
 */

@Entity
public class Sensor extends Model {
    @Id
    @GeneratedValue
    private Long OID;
    private String sensorToken;
    private String label;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Resource> resources;

    public Sensor(String label){
        this.label = label;
        this.sensorToken = UUID.randomUUID().toString();
        this.resources = new ArrayList<Resource>();
    }

    public ObjectNode toJson(){
        ObjectNode sensor = Json.newObject();
        sensor.put("sensorToken",this.sensorToken);
        sensor.put("label",this.label);
        return sensor;
    }


    public String getSensorToken() {
        return sensorToken;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public Long getOID() {
        return OID;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public static Finder<Long,Sensor> find = new Finder<Long,Sensor>( Long.class, Sensor.class );

}
