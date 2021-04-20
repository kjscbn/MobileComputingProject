/*********************************************************************
Quick and dirty nodejs test harness for hopefully human clients

Listens for connections on port 9999.

When data is received, parse JSON and return a hardcoded score based
on activity type
***********************************************************************/

var net = require('net');

var server = net.Server();

server.listen({port: 9999});

server.on("connection", client => {
  client.setEncoding('ascii');

  var inBuf = "";

  // got some data
  client.on("data", data => {
    inBuf += data;

    // if we got an "EOT" character (0x04) that's the end of the message
    if(inBuf.charCodeAt(inBuf.length - 1) == 4) {
      var score;
      var errText;

      rawRequest = inBuf.slice(0, -1) // strip off the EOT

      try {
        request = JSON.parse(rawRequest);
        subjectIP = request.subjectIP;

        if(!request.actType) {
          errText = "invalid request, actType required";
        }
        else {
          switch(request.actType) {
            case 'REQ':
              console.log('request');
              score = 99
              break;
            case 'GEO':
              console.log('location');
              score = 79
              break;
            case 'WEB':
              console.log('web');
              score = 89
              break;
            default:
              errText = "invalid request, unknown actType";
          }
        }

        var response;

        if(errText) {
          console.log(errText);

          response = {
            error: errText
          }
        }
        else {
          response = {
            subjectIP: subjectIP,
            score: score
          }
        }

        client.write(JSON.stringify(response, null, 2));
      }
      catch(ex) {
        console.log('error parsing JSON', rawRequest);
      }

      client.end();
    }
  });
});
