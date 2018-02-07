
package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Configuration
public class QuoteConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this package must match the package in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("br.com.claro.ebs.claro.v1");
		return marshaller;
	}

	@Bean
	public SoapClient quoteClient(Jaxb2Marshaller marshaller) {
		SoapClient client = new SoapClient();
		client.setDefaultUri("http://brux0338:9200/esb/services/ConsultarUltimoAssinantev1");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
	
	@Bean
	   public Jackson2ObjectMapperBuilder jacksonBuilder() {
	       Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
	       b.indentOutput(true);
	       b.featuresToEnable(DeserializationFeature.READ_ENUMS_USING_TO_STRING,
	               DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

	       JaxbAnnotationModule module = new JaxbAnnotationModule();
	       b.modules(module);
	       return b;
	   }

	   @Bean
	   ObjectMapper objectMapper() {
	       return jacksonBuilder().build();
	   }

}
