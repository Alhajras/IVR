package agi.script;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

public class HelloAgiScriptText  extends BaseAgiScript {

	@Override
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		answer();
		exec("Gosub", "sending-PIN", "${EXTEN:1}", "1");
		hangup();
		
	}

}
