<!DOCTYPE html>
<html>
<head>
<title>Welcome to Hopefully Human!</title>
<meta name="viewport" content="width=device-width" />
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to Hopefully Human!</h1>
<?php
  $ip  = $_SERVER['REMOTE_ADDR'];
  $url = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
  $ua  = $_SERVER['HTTP_USER_AGENT'];

  echo "<p>Your IP is $ip</p>";

  if(!isset($_SERVER["HTTP_SUBJECTTOKEN"])) {
    echo "<p>You do not have an HH token set</p>";
  }
  else {
    $token  = $_SERVER['HTTP_SUBJECTTOKEN'];
    echo "<p>Your HH token is $token</p>";

    $host = "localhost";
    $port = 7777;

    $sock = socket_create(AF_INET, SOCK_STREAM, 0);
    $rc = socket_connect($sock, $host, $port);

    if($rc) {
      // request JSON
      $req = "{";
      $req .="\"subjectIP\": \"$ip\", \"actType\": \"WEB\", ";
      $req .="\"URL\": \"$url\", \"userAgent\": \"$ua\"";
      if(isset($token)) {
        $req .= "\"subjectToken\":\"$token\", ";
      }
      $req .= "}";

      echo "Request to HH Score Server:<br> <code>$req</code>";

      $req .= chr(4); // add EOT character so server knows we're done

      socket_write($sock, $req, strlen($req));

      echo "<br><br>HH Score Server Response:<br> <code>";

      while ($out = socket_read($sock, 2048)) {
        echo $out;
      }

      echo "</code>";
    }
    else {
      echo "Couldn't connect to HH server.";
    }
  }
?>

<p><a href="ads.apk">Download the Android Data Service</a></p>

<p><a href="asc.apk">Download the Android Sample Client</a></p>

<em>Please note:  you will need to disable android play protect in order to install these APK's</em>
</body>
</html>
