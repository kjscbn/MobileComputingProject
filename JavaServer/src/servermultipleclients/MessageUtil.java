package servermultipleclients;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MessageUtil {
  // read a raw string containing a request messages from the data input stream
  // input message is terminated by end of file or ASCII EOT character 0x04
  public static String readRequest(DataInputStream in) throws IOException {
    byte[] buf = new byte[1024];
    int bytesRead;
    boolean eot = false; // message terminator flag

    String rawResponse = "";

    // read bytes from the buffer until closed or EOT seen
    while(!eot && (bytesRead = in.read(buf)) != -1) {
      // if last byte is 0x04, we're done, (but don't add it to return string)
      if(buf[bytesRead - 1] == 4) {
        eot = true;
        bytesRead--;
      }

      rawResponse += new String(buf, 0, bytesRead);
    }

    return rawResponse;
  }

  // read a raw string response message - since readRequest terminates
  // on either EOF or EOT, we just call it
  public static String readResponse(DataInputStream in) throws IOException {
    return readRequest(in);
  }

  // write a raw EOT-terminated request string to the data output stream
  public static void writeRequest(DataOutputStream out, String msg) throws IOException {
    out.writeBytes(msg.toString());
    out.writeByte(4);
  }
 
  // write a raw response string to the provided data output stream
  public static void writeResponse(DataOutputStream out, String msg) throws IOException {
    out.writeBytes(msg.toString());
  }
 
}
