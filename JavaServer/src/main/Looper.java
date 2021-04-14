package main;

import java.util.concurrent.atomic.AtomicBoolean;

public class Looper implements Runnable{
	//Bug when stopping server.
			Servers server = null;
			
			private AtomicBoolean keepRunning;
			
			public Looper() {
				keepRunning = new AtomicBoolean(true);
			}
			
			public void stop() {
				keepRunning.set(false);
				server = null;
			}
			
			@Override
			public void run() {
				while(keepRunning.get()) {
					//This is where server code will go. Sockets, connections, all that stuff
					server = new Servers(5000);
				}
			}
}
