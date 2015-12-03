package actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.*;

import java.util.ArrayList;
import java.util.List;

import actors.messages.*;
import californium.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.ObjectNode;

/**
 * This actor handles initialization of the Californium CoAP server and all communication
 * between said server and the Play application. Other actors can register themselves
 * (through the SendMeCoapMessages message) with this actor in order to have CoAP messages
 * forwarded to them.
 */
public class CaliforniumServerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    CaliforniumServer server;

    // A list of actors that have registered themselves to receive CoAP messages
    private List<ActorRef> coapRecipients;

    public CaliforniumServerActor() {
        super();
        server = CaliforniumServer.initialize(getSelf());
        coapRecipients = new ArrayList<ActorRef>();
    }

    public void onReceive(Object message) throws Exception {

        if(message instanceof ShutdownActor) {
            log.info("Graceful shutdown of the CaliforniumServer and the CaliforniumServerActor");
            server.stop();
            getSelf().tell(akka.actor.PoisonPill.getInstance(), getSelf());

        } else if(message instanceof CoapMessageReceived) {
            CoapMessageReceived msg = (CoapMessageReceived) message;
            log.info("Received CoAP message: '{}'", msg.getMessage());

        } else {
            log.info("Unhandled message: {}", message);
            unhandled(message);
        }
    }
}
