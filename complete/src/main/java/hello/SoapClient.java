
package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class SoapClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(SoapClient.class);

	public Object call(String wsdl,String callback, Object request) {

		log.info("Starting Requesting SoapClient");

		Object response = getWebServiceTemplate()
				.marshalSendAndReceive(wsdl,
						request,
						new SoapActionCallback(callback));

		log.info("Finished Requesting SoapClient");
		
		return response;
	}

}
