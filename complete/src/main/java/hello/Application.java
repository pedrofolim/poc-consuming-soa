
package hello;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import hello.wsdl.GetQuote;
import hello.wsdl.GetQuoteResponse;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	CommandLineRunner generateRequestJsonFromObjectUsingFasterXML(){
		return args -> {
			ObjectMapper mapper = new ObjectMapper();
			JaxbAnnotationModule module = new JaxbAnnotationModule();
			mapper.registerModule(module);
			// configure mapper, if necessary, then create schema generator
			JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
			JsonSchema schema = schemaGen.generateSchema(GetQuote.class);
			System.out.println(mapper.writeValueAsString(schema));
		};
    }
	
	@Bean
	CommandLineRunner generateResponseJsonFromObjectUsingFasterXML(){
		return args -> {
			ObjectMapper mapper = new ObjectMapper();
			JaxbAnnotationModule module = new JaxbAnnotationModule();
			mapper.registerModule(module);
			// configure mapper, if necessary, then create schema generator
			JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
			JsonSchema schema = schemaGen.generateSchema(GetQuoteResponse.class);
			System.out.println(mapper.writeValueAsString(schema));
		};
    }

	@Bean
	CommandLineRunner lookup(SoapClient soapClient) {
		return args -> {
			String wsdl = "http://www.webservicex.com/stockquote.asmx";
			String callback = "http://www.webserviceX.NET/GetQuote";
			
			FlowContext context = new FlowContext();
			context.getSteps().add(new Step("Obter Stoque","MSFT"));
			
			String response = soapClient.call(wsdl, callback, context);
			
			System.err.println(response);
		};
	}
	
	@Bean
	CommandLineRunner generateXmlFromObjectUsingJaxb(){
		return args -> {
	        JAXBContext restJC;
			try {
				restJC = JAXBContext.newInstance(GetQuote.class);
				GetQuote quote = new GetQuote();
		        Marshaller jaxbMarshaller = restJC.createMarshaller();
				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				//jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
				//jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
				jaxbMarshaller.marshal(quote, System.out);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
    }
}
