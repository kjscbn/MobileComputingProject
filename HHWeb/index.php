<!DOCTYPE html>
<html>
<head>
<title>Welcome to Hopefully Human!</title>
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

  $host = "localhost";
  $port = 9999;

  $sock = socket_create(AF_INET, SOCK_STREAM, 0);
	$rc = socket_connect($sock, $host, $port);

  if($rc) {
    // request JSON
    $req = "{";
    $req .="\"subjectIP\": \"$ip\", \"actType\": \"WEB\", ";
    $req .="\"URL\": \"$url\", \"userAgent\": \"$ua\"";
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


?>


</body>
</html>
