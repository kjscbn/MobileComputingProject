<!DOCTYPE html>
<html>
<head>
  <title>Good News Today!</title>
  <meta name="viewport" content="width=device-width, user-scalable=no" />
  <style>
    body {
      width: 35em;
      margin: 0 auto;
      font-family: Tahoma, Verdana, Arial, sans-serif;
    }
    p {
      font-size:24px
    }
  </style>
</head>
<body>
<?php
  if(isset($_SERVER["HTTP_SUBJECTTOKEN"])) {
    $token = $_SERVER["HTTP_SUBJECTTOKEN"];
    $ip  = $_SERVER['REMOTE_ADDR'];
    $url = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
    $ua  = $_SERVER['HTTP_USER_AGENT'];

    $host = "localhost";
    $port = 7777;

    $sock = socket_create(AF_INET, SOCK_STREAM, 0);
    $rc = socket_connect($sock, $host, $port);

    if($rc) {
      // request JSON
      $req = "{";
      $req .="\"subjectToken\": \"${token}\", \"actType\": \"REQ\"}";
      $req .= chr(4); // add EOT character so server knows we're done
      socket_write($sock, $req, strlen($req));

      $resp = "";
      while ("" != ($chunk = socket_read($sock, 2048))) {
        $resp .= $chunk;
      }

      $json_resp = json_decode($resp);
      $score = $json_resp->score;
    }
    else {
      echo "Sorry, couldn't connect to HH server.";
    }
  }
?>

<?php if(!isset($score)) : ?>
<h1>No news for you!</h1>

<p>You have to have a token if you want to see the latest news</p>

<?php elseif($score >= 0) : ?>
<h1>Here's some good news for you today!</h1>

<p>This is some great news that you didn't know about until you read it just now!</p>
<?php else : ?>
<h1>Bad News!</h1>

<p>We can't give you any good news right now.  Please answer a quick question to prove you're human and not a content scraper</p>

<button>Click me</button>
<?php endif; ?>

</body>
</html>
