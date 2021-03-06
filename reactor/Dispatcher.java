package reactor;

import java.util.ArrayList;
import java.util.HashMap;

import reactorapi.*;
import reactorexample.AcceptHandle;
import reactorexample.TCPTextHandle;
import reactor.BlockingEventQueue;
import reactor.WorkerThread;

public class Dispatcher {
	public Dispatcher() {
		this(10);
	}

	public Dispatcher(int capacity) {
		// TODO: Implement Dispatcher(int).
		this.queue = new BlockingEventQueue<EventHandler<?>>(capacity);
	}

	public void handleEvents() throws InterruptedException {
		// TODO: Implement Dispatcher.handleEvents().
		
		// wait until there is an event. When an event is
		// received then first event is read and then removed
		
		if (handlerMap.isEmpty()) {
			return;
		}
		
		while(!handlerMap.isEmpty())
		{
			Event<?> event = select();
			EventHandler<?> handler = event.getHandler();
			if (handlerMap.containsKey(handler)) {
				event.handle();
				if (event.getEvent() == null) {
					removeHandler(handler);
				}
			}		
		}
		
		
	
	}

	public Event<?> select() throws InterruptedException {
		//throw new UnsupportedOperationException();
		// TODO: Implement Dispatcher.select().
		
		// Wait until an event is received on any registered handle
		Event<?> event = queue.get();
		return event;
	}

	public void addHandler(EventHandler<?> h) {
		// TODO: Implement Dispatcher.addHandler(EventHandler).
		
		//Register an unregistered handler; i.e. start dispatching incoming events for h. 
		//All events received on the corresponding handle (h.getHandle()) must be (eventually) 
		//dispatched to h, until removeHandler(h) is called or a null message is received
		
		if (!handlerMap.containsKey(h)) 
		{
			WorkerThread<?> workerThread = new WorkerThread(h, queue);
			workerThread.start();
			handlerMap.put(h, workerThread);
			numberOfActiveWorkerThreads ++;
		}
		
	}

	public void removeHandler(EventHandler<?> h) {
		if (handlerMap.containsKey(h)) {
			WorkerThread <?> thread = handlerMap.get(h);
			thread.cancelThread();
			handlerMap.remove(h);
		}
	}
		

	// Add methods and fields as needed.
	private BlockingEventQueue<EventHandler<?>> queue;
	//private ArrayList<EventHandler<?>> eventHandlers = new ArrayList<EventHandler<?>>();
	private HashMap<EventHandler<?>, WorkerThread<?>> handlerMap = new HashMap<EventHandler<?>, WorkerThread<?>>();
	public int numberOfActiveWorkerThreads = 0;
			
}
