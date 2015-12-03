# Play-Californium

This is an example application demonstrating how the [Eclipse Californium](https://www.eclipse.org/californium/) CoAP server can be integrated into a Java/Scala [Play web framework](https://playframework.com/) application. Find out more at [Watr.li](http://watr.li).

## usage

To start the server, run

    ./activator run

Wait for the following output to show up:

    --- (Running the application, auto-reloading is enabled) ---

    [info] play - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

    (Server started, use Ctrl+D to stop and go back to the console...)

Now, visit

    http://localhost:9000

The server is now ready to accept PUT requests.