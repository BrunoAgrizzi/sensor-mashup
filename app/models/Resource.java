package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by agrizzi-californium on 28/11/15.
 */
@Entity
public class Resource extends Model {
    @Id
    @GeneratedValue
    private Long OID;
    private String resourceToken;
    private String label;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ResourceData> data;

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @ManyToOne
    private Sensor sensor;

    public Resource(String label){
        this.label = label;
        this.resourceToken = UUID.randomUUID().toString();
        this.data = new ArrayList<ResourceData>();
    }
    public ObjectNode toJson(){
        ObjectNode resource = Json.newObject();
        resource.put("resourceToken",this.resourceToken);
        resource.put("label",this.label);
        return resource;
    }

    public List<ResourceData> getData() {
        return data;
    }

    public void setData(List<ResourceData> data) {
        this.data = data;
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public void setResourceToken(String resourceToken) {
        this.resourceToken = resourceToken;
    }

    public static Finder<Long,Resource> find = new Finder<Long,Resource>( Long.class, Resource.class );
}
