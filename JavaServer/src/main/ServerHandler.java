package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerHandler implements ActionListener{
	private Looper server;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Server.getStartButton()) {
			if(server == null) {
				server = new Looper();
				Thread t = new Thread(server);
				t.start();
			}
		}else if(e.getSource() == Server.getStopButton()){
			server.stop();
			server = null;
		}
	}
}
