package agi.script;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

public class SendMessages extends BaseAgiScript {

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		exec("SendText", "I hate my life");
		hangup();
	}

}
