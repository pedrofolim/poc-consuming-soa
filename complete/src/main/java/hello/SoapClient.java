
package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import hello.wsdl.GetQuote;
import hello.wsdl.GetQuoteResponse;

public class SoapClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(SoapClient.class);

	public String call(String wsdl,String callback, FlowContext context) {

		GetQuote request = new GetQuote();
		request.setSymbol("");

		log.info("Requesting SoapClient for " + context.toString());

		GetQuoteResponse response = (GetQuoteResponse) getWebServiceTemplate()
				.marshalSendAndReceive(wsdl,
						request,
						new SoapActionCallback(callback));
		
		log.info("Response Result ", response.getGetQuoteResult());

		return response.getGetQuoteResult();
	}

}
