package global;


import models.Resource;
import models.ResourceData;
import models.Sensor;
import play.*;
import play.libs.Akka;

import akka.actor.*;

import actors.CaliforniumServerActor;
import actors.messages.*;


public class Global extends GlobalSettings {

    private static ActorRef californiumActor;

    @Override
    public void onStart(Application app) {
        californiumActor = Akka.system().actorOf(Props.create(CaliforniumServerActor.class));

        Sensor sensor = new Sensor("Sensor 001");
        Resource resource = new Resource("Temperatura");
        sensor.getResources().add(resource);
        resource.setSensor(sensor);
        ResourceData data = new ResourceData(40,1449178504420L);
        resource.getData().add(data);
        data.setResource(resource);
        sensor.save();
        resource.save();
        data.save();
    }
    @Override
    public void onStop(Application app) {

        californiumActor.tell(new ShutdownActor(), null);
    }

    public static ActorRef getCaliforniumActor() {

        return californiumActor;
    }
}
