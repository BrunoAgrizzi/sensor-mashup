package global;


import models.Resource;
import models.ResourceData;
import models.Sensor;
import play.*;
import play.libs.Akka;

import akka.actor.*;

import actors.CaliforniumServerActor;
import actors.messages.*;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.SimpleResult;


import play.libs.F.Promise;

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

    // For CORS
    private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

        @Override
        public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
            Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            response.setHeader("Access-Control-Allow-Origin", "*");
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request,
                               java.lang.reflect.Method actionMethod) {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }

}
