package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import akka.actor.*;
import play.libs.F.*;
import play.mvc.WebSocket;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

}
