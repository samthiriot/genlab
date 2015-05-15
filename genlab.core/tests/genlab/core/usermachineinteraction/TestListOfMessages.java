package genlab.core.usermachineinteraction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestListOfMessages {


	@Test(timeout=1000)
	public void testaddAMessage() {
		
		ListOfMessages msgs = new ListOfMessages();
		
		msgs.debugTech("test1", getClass());
		
		msgs.waitUntilMessagesQueueConsumed();
		
		assertEquals("the message was not added", 1, msgs.getSize());
		
	}
	
	@Test(timeout=50000)
	public void testaddManyMessages() {
		
		final int messagesToAdd = 10000;
		
		ListOfMessages msgs = new ListOfMessages(null, null, Integer.MAX_VALUE, Integer.MAX_VALUE/2);
		
		for (int i=0; i<messagesToAdd; i++) {
			
			msgs.debugTech("test "+i, getClass());
		}
		
		msgs.waitUntilMessagesQueueConsumed();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("the message was not added", messagesToAdd, msgs.getSize());
		
	}
	
	@Test(timeout=50000)
	public void testaddManyMessagesMultiThread() {
		
		final int toAddPerThread = 5000;
		final int nbThreads = 10;
		
		final ListOfMessages msgs = new ListOfMessages(null, null, toAddPerThread*nbThreads + 3000);
		
		
		class ThreadAddManyMessages extends Thread {
			
			private final String toto;
			private final int start;
			
			public ThreadAddManyMessages(String toto, int start) {
				this.toto = toto;
				this.start = start;
			}
			
			@Override
			public void run() {
				for (int i=start; i<(toAddPerThread+start); i++) {
					if (i % toAddPerThread/100 == 0)
						Thread.yield();
					msgs.debugTech("test "+i+" "+toto, getClass());
				}
			}
			
		}
		
		
		for (int i = 0; i<nbThreads; i++) {
			Thread t = new ThreadAddManyMessages("thread "+i, toAddPerThread*i+1);
			t.start();
		}
		
		msgs.waitUntilMessagesQueueConsumed();
		
		assertEquals("the message was not added", toAddPerThread*nbThreads, msgs.getSize());
		
	}


}
