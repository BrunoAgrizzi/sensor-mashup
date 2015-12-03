package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by agrizzi-californium on 28/11/15.
 */
@Entity
public class ResourceData extends Model {
    @Id
    @GeneratedValue
    private Long OID;
    private double value;
    private long timestamp;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @ManyToOne
    private Resource resource;

    public ResourceData(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public ObjectNode toJson(){
        ObjectNode sensor = Json.newObject();
        sensor.put("value",this.value);
        sensor.put("timestamp",this.timestamp);
        return sensor;
    }


    public double getValue() {
        return value;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public static Model.Finder<Long,ResourceData> find = new Model.Finder<Long,ResourceData>( Long.class, ResourceData.class );
}
