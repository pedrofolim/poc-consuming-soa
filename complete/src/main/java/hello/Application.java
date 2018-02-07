
package hello;

import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.WriteContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1ArrayOfSegmentacaoType;
import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1AssinanteType;
import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1ClienteType;
import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1ContratoType;
import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1DispositivoType;
import br.com.claro.ebo.claro.v1.ConsultarUltimoAssinantev1SegmentacaoType;
import br.com.claro.ebs.claro.v1.ConsultarUltimoAssinanteRequest;
import br.com.claro.ebs.claro.v1.ConsultarUltimoAssinanteResponse;

@SpringBootApplication
public class Application {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	JSONParser parser = new JSONParser();
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	
	@Bean
	CommandLineRunner generateXmlFromObjectUsingJaxb(){
		return args -> {
	        JAXBContext restJC;
			try {
				System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
				
				restJC = JAXBContext.newInstance(ConsultarUltimoAssinanteRequest.class);
	
				ConsultarUltimoAssinanteRequest request = new ConsultarUltimoAssinanteRequest();
                ConsultarUltimoAssinantev1AssinanteType assinanteType = new ConsultarUltimoAssinantev1AssinanteType();
                ConsultarUltimoAssinantev1DispositivoType dispType = new ConsultarUltimoAssinantev1DispositivoType();
                assinanteType.setAparelho(dispType);
                dispType.setMsisdn("112213123");
                assinanteType.setNome("nome");
                assinanteType.setContrato(new ConsultarUltimoAssinantev1ContratoType());
                assinanteType.getContrato().setCliente(new ConsultarUltimoAssinantev1ClienteType());
                assinanteType.getContrato().getCliente().setSegmentacoes(new ConsultarUltimoAssinantev1ArrayOfSegmentacaoType());
                ConsultarUltimoAssinantev1SegmentacaoType segmentacao = new ConsultarUltimoAssinantev1SegmentacaoType();
                segmentacao.setSegmentacaoId("id segmentacao");
                assinanteType.getContrato().getCliente().getSegmentacoes().getSegmentacao().add(segmentacao);
                request.setAssinante(assinanteType);
                
		        Marshaller jaxbMarshaller = restJC.createMarshaller();
				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
				jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
				jaxbMarshaller.marshal(request, System.out);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
    }
	
	
	@Bean
	CommandLineRunner generateRequestJsonFromObjectUsingFasterXML(){
		return args -> {
			ObjectMapper mapper = new ObjectMapper();
			JaxbAnnotationModule module = new JaxbAnnotationModule();
			mapper.registerModule(module);
			// configure mapper, if necessary, then create schema generator
			JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
			JsonSchema schema = schemaGen.generateSchema(ConsultarUltimoAssinanteRequest.class);
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
			JsonSchema schema = schemaGen.generateSchema(ConsultarUltimoAssinanteResponse.class);
			log.info("Response JSON generate {}",mapper.writeValueAsString(schema));
		};
    }

	@Bean
	CommandLineRunner lookup(SoapClient soapClient, ObjectMapper mapper) {
		return args -> {
			//urls
			String wsdl = "http://brux0338:9200/esb/services/ConsultarUltimoAssinantev1";
			String callback = "http://www.claro.com.br/EBS/Claro/v1";
			
			//context flow
			FlowContext context = new FlowContext();
			Step step = new Step("Obter Assinante","11940590305");
			step.getInternalAttributes().add("segmentacao1");
			step.getInternalAttributes().add("segmentacao2");
			step.getInternalAttributes().add("segmentacao3");
			context.getSteps().add(step);
					
			//converte o contexto in json
			String contextString = mapper.writeValueAsString(context);
			//Carrega target
			JSONObject target = (JSONObject) parser.parse(new FileReader(
                    "/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/taskSoapRequestTemplate.json"));
			//Carrega mapping input
			JSONObject inputMapping = (JSONObject) parser.parse(new FileReader(
                    "/Users/folim/workspace/sysmap/pocs/gs-consuming-web-service/complete/src/main/resources/TaskSoap/inputTaskDataMapping.json"));
		
			//realizar data mapping
			String targetMapped = dataMapping(contextString, "{}", inputMapping.toJSONString());
			
			//convert json para Object
			ConsultarUltimoAssinanteRequest request = mapper.readValue(targetMapped, ConsultarUltimoAssinanteRequest.class);
			
			//chama serviço
			ConsultarUltimoAssinanteResponse response = (ConsultarUltimoAssinanteResponse) soapClient.call(wsdl, callback, request);
			
			//transforma response em json
			
			//realiza data mapping de saida
			
			log.info("Response after call Soap Task {}", response);
		};
	}
	
	@SuppressWarnings("unchecked")
	private String dataMapping(String source, String target, String mapping) throws ParseException {
		
        //final Configuration hubConfig = Configuration.builder().mappingProvider(new HubDataMappingTemplateProvider()).build();
        final Configuration hubConfig = Configuration.builder()
        			.jsonProvider(new JacksonJsonProvider())
        			.mappingProvider(new JacksonMappingProvider())
        			.options(Option.DEFAULT_PATH_LEAF_TO_NULL).build();

		log.info("Loading values from source content {}", source);
		final ReadContext sourceCtx = JsonPath.using(hubConfig).parse(source);
		log.info("Loading values from target content {}", target);
        WriteContext targetCtx = JsonPath.using(hubConfig).parse(target);
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
            		targetCtx.set(targetPath, sourceValue);
        		}catch (PathNotFoundException e) {
        			log.error("path in {} or path out {} not mapped in datamapping file", sourcePath, targetPath);
			}
        });
        
        log.info("connections for datamapping mapped {}", targetCtx.jsonString());
        return targetCtx.jsonString();
	}
}
