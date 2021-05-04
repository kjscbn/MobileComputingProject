<!DOCTYPE html>
<html>
<head>
<title>Welcome to Hopefully Human!</title>
<meta name="viewport" content="width=device-width" />
<style>
    body {
        max-width: 80em;
        margin: 20 auto;
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

  $welcomeImage = "neither.png";
  $welcomeMessage = "We're not really sure what you are";

  echo "<p>Your IP is $ip ";


  if(isset($_SERVER["HTTP_SUBJECTTOKEN"])) {
    $token  = $_SERVER['HTTP_SUBJECTTOKEN'];
  }
  elseif(isset($_GET["subjectToken"])) {
    $token  = $_GET["subjectToken"];
  }

  if(isset($token)) {
    echo "and your HH token is $token</p>";

    $host = "localhost";
    $port = 7777;

    $sock = socket_create(AF_INET, SOCK_STREAM, 0);
    $rc = socket_connect($sock, $host, $port);

    if($rc) {
      // request JSON
      $req = "{";
      $req .="\"subjectIP\": \"$ip\", \"actType\": \"WEB\",";
      $req .="\"actData\":{";
      $req .="\"url\": \"$url\", \"userAgent\": \"$ua\"";
      $req .="}";
      if(isset($token)) {
        $req .= ",\"subjectToken\":\"$token\"";
      }
      $req .= "}";

      echo "Request to HH Score Server:<br> <code>$req</code>";

      $req .= chr(4); // add EOT character so server knows we're done

      socket_write($sock, $req, strlen($req));

      $resp = "";
      while ("" != ($out = socket_read($sock, 2048))) {
        $resp .= $out;
      }

      echo "<br><br>HH Score Server Response:<br> <code>";
      echo $resp;
      echo "</code>";

      $data = json_decode($resp);

      if($data->score < 0) :
        $welcomeImage = "bot.png";
        $welcomeMessage = "We're betting you're a bot!";
      else :
        $welcomeImage = "human.png";
        $welcomeMessage = "We're hopeful that you are human!";
      endif;
    }
    else {
      echo "Couldn't connect to HH server.";
    }
  }
  else {
    echo "and do not have an HH token set</p>";
  }
?>

<div style="text-align:center"><img height=300px src="img/<?=$welcomeImage?>"></div>
<h1 style="text-align:center"><?=$welcomeMessage?></h1>

<p><a href="ads.apk">Download the Android Data Service</a></p>

<p><a href="asc.apk">Download the Android Sample Client</a></p>

<em>Please note:  you may need to disable android play protect in order to install these APK's</em>
</body>
</html>
