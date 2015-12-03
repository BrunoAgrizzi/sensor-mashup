var ws = new WebSocket("ws://" + window.location.host + "/websocket");

ws.onmessage = function( message ) {
    console.log(message);
    document.getElementById('coapMessages').innerHTML += "<p>" + message.data + "</p>";
};

