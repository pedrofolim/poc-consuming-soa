
package hello;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.json.simple.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
			System.out.println(String.format("Request JSON generate ",mapper.writeValueAsString(schema)));
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
			System.out.println(String.format("Response JSON generate ",mapper.writeValueAsString(schema)));
		};
    }

	@Bean
	CommandLineRunner lookup(SoapClient soapClient) {
		return args -> {
			
			JSONParser parser = new JSONParser();
			
			//urls
			String wsdl = "http://www.webservicex.com/stockquote.asmx";
			String callback = "http://www.webserviceX.NET/GetQuote";
			
			//context flow
			FlowContext context = new FlowContext();
			context.getSteps().add(new Step("Obter Stoque","MSFT"));
					
			//Carrega arquivo json input
			Object teste = parser.parse(new FileReader(
                    "/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/taskSoapRequestTemplate.json"));
		
			//realizar data mapping
			
			//convert json para Object
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JaxbAnnotationModule module = new JaxbAnnotationModule();
			mapper.registerModule(module);
			GetQuote request = mapper.readValue(new File("/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/taskSoapRequestTemplate.json"), GetQuote.class);
			request.setSymbol("MSFT");
			
			//chama serviço
			String response = soapClient.call(wsdl, callback, request);
			
			//transforma response em json
			
			//realiza data mapping de saida
			
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
