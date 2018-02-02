
package hello;

import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.WriteContext;

import hello.wsdl.GetQuote;
import hello.wsdl.GetQuoteResponse;

@SpringBootApplication
public class Application {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	JSONParser parser = new JSONParser();
	
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
			log.info("Request JSON generate {}",mapper.writeValueAsString(schema));
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
			log.info("Response JSON generate {}",mapper.writeValueAsString(schema));
		};
    }

	@Bean
	CommandLineRunner lookup(SoapClient soapClient) {
		return args -> {
			//mapper
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JaxbAnnotationModule module = new JaxbAnnotationModule();
			mapper.registerModule(module);
			
			//urls
			String wsdl = "http://www.webservicex.com/stockquote.asmx";
			String callback = "http://www.webserviceX.NET/GetQuote";
			
			//context flow
			FlowContext context = new FlowContext();
			context.getSteps().add(new Step("Obter Stoque","MSFT"));
					
			//converte o contexto in json
			String contextString = mapper.writeValueAsString(context);
			//Carrega target
			JSONObject target = (JSONObject) parser.parse(new FileReader(
                    "/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/taskSoapRequestTemplate.json"));
			//Carrega mapping input
			JSONObject inputMapping = (JSONObject) parser.parse(new FileReader(
                    "/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/inputTaskDataMapping.json"));
		
			//realizar data mapping
			String targetMapped = dataMapping(contextString, target.toJSONString(), inputMapping.toJSONString());
			
			//convert json para Object
			GetQuote request = mapper.readValue(targetMapped, GetQuote.class);
			request.setSymbol("MSFT");
			
			//chama serviço
			GetQuoteResponse response = (GetQuoteResponse) soapClient.call(wsdl, callback, request);
			
			//transforma response em json
			
			//realiza data mapping de saida
			
			log.info("Response after call Soap Task {}", response);
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
	
	@SuppressWarnings("unchecked")
	private String dataMapping(String source, String target, String mapping) throws ParseException {
		
		log.info("Loading values from source content {}", source);
		final ReadContext sourceCtx = JsonPath.parse(source);
		log.info("Loading values from target content {}", target);
        WriteContext targetCtx = JsonPath.parse(target);
        log.info("Reading string mapping and transform in json object {}", mapping);
        
        JSONObject mappingObj = (JSONObject) parser.parse(mapping);
        JSONArray connections = (JSONArray) mappingObj.get("connections");
        log.info("Reading connections for datamapping  {}", connections);
     
        connections.forEach(connection->{
        		final JSONObject connectionJSON = (JSONObject) connection;
        		final JSONObject sourcePort = (JSONObject)connectionJSON.get("sourcePort");
        		final JSONObject targetPort = (JSONObject)connectionJSON.get("targetPort");
        		final String sourcePath = (String)sourcePort.get("path");
        		final String targetPath = (String)targetPort.get("path");
        		
        		try {
        			final Object sourceValue = sourceCtx.read(sourcePath);
            		targetCtx.set(targetPath, sourceValue.toString());
        		}catch (PathNotFoundException e) {
        			log.error("path in {} or path out {} not mapped in datamapping file", sourcePath, targetPath);
			}
        });
        
        return targetCtx.jsonString();
	}
}
