package ltr.org.bankmaster.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Value("${swagger.service.config.name}")
    private String serviceName;
    @Value("${swagger.service.config.description}")
    private String serviceDescription;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket commonMasterApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.GET,
                        singletonList(new ResponseBuilder()
                                .code("500")
                                .description("500 message")
                                .representation(MediaType.TEXT_XML)
                                .apply(r ->
                                        r.model(m ->
                                                m.referenceModel(ref ->
                                                        ref.key(k ->
                                                                k.qualifiedModelName(q ->
                                                                        q.namespace("some:namespace")
                                                                                .name("ERROR"))))))
                                .build()))
                .securitySchemes(singletonList(apiKey()))
                .securityContexts(singletonList(securityContext()))
                .enableUrlTemplating(true)
                .globalRequestParameters(
                        singletonList(new springfox.documentation.builders.RequestParameterBuilder()
                                //.name("someGlobalParameter")
                                //	.description("Description of someGlobalParameter")
                                .in(ParameterType.QUERY)
                                .required(true)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .build()))
                .tags(new Tag(serviceName, serviceDescription))
                ;
    }
    private ApiKey apiKey() {
        return new ApiKey("mykey", "api_key", "header");
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                //	.securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/anyPath.*"))
                .build();
    }
    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .showCommonExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }

    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                                                         ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                                         EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
                                                                         WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment,
                basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
                corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping, null);
    }

    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
                                               String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
                || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
}
