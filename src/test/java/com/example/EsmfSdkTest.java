package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.aspectmodel.jackson.AspectModelJacksonModule;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.catenax.part_as_planned.PartAsPlanned;
import org.junit.jupiter.api.Test;

public class EsmfSdkTest {
   private static final String BASE_DIR = "sldt-semantic-models";
   private static final ResolutionStrategy FILE_SYSTEM_STRATEGY = new FileSystemStrategy( Path.of( BASE_DIR ) );

   @Test
   void loadAspectModelByUrn() {
      final AspectModelUrn aspectUrn = AspectModelUrn.fromUrn( "urn:samm:io.catenax.part_as_planned:2.0.0#PartAsPlanned" );

      assertThatCode( () -> {
         final AspectModel aspectModel = new AspectModelLoader( FILE_SYSTEM_STRATEGY ).load( aspectUrn );
         assertThat( aspectModel.aspects() ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @Test
   void loadAspectModelByFile() {
      final File file = new File( System.getProperty( "user.dir" ) ).toPath()
            .resolve( BASE_DIR )
            .resolve( "io.catenax.part_as_planned" )
            .resolve( "2.0.0" )
            .resolve( "PartAsPlanned.ttl" )
            .toFile();

      assertThatCode( () -> {
         final AspectModel aspectModel = new AspectModelLoader( FILE_SYSTEM_STRATEGY ).load( file );
         assertThat( aspectModel.aspects() ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @Test
   void loadAspectModelByInputStream() throws IOException {
      final File file = new File( System.getProperty( "user.dir" ) ).toPath()
            .resolve( BASE_DIR )
            .resolve( "io.catenax.part_as_planned" )
            .resolve( "2.0.0" )
            .resolve( "PartAsPlanned.ttl" )
            .toFile();

      try ( final InputStream input = new FileInputStream( file ) ) {
         assertThatCode( () -> {
            final AspectModel aspectModel = new AspectModelLoader( FILE_SYSTEM_STRATEGY ).load( input );
            assertThat( aspectModel.aspects() ).isNotEmpty();
         } ).doesNotThrowAnyException();
      }
   }

   @Test
   void generateSampleJsonAndParseJson() throws IOException {
      final AspectModelUrn aspectUrn = AspectModelUrn.fromUrn( "urn:samm:io.catenax.part_as_planned:2.0.0#PartAsPlanned" );
      final AspectModel aspectModel = new AspectModelLoader( FILE_SYSTEM_STRATEGY ).load( aspectUrn );
      final Aspect aspect = aspectModel.aspects().stream().filter( a -> a.urn().equals( aspectUrn ) ).findFirst().orElseThrow();

      // Generate sample JSON payload
      final AspectModelJsonPayloadGenerator jsonGenerator = new AspectModelJsonPayloadGenerator( aspect );
      final String sampleJson = jsonGenerator.generateJson();

      System.out.println( "JSON for " + aspectUrn + ":" );
      System.out.println( sampleJson );
      System.out.println();

      // Create Jackson object mapper
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new Jdk8Module() );
      mapper.registerModule( new AspectModelJacksonModule() );
      mapper.configure( JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );

      assertThatCode( () -> {
         // Parse JSON. We now have a type-safe representation of Aspect data.
         final PartAsPlanned partAsPlanned = mapper.readValue( sampleJson, PartAsPlanned.class );

         // Print the object
         final String objectAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( partAsPlanned );
         System.out.println( "Java object with data:" );
         System.out.println( objectAsString );
      } ).doesNotThrowAnyException();
   }

   @Test
   void generateDocumentationForAspectModel() throws IOException {
      final AspectModelUrn aspectUrn = AspectModelUrn.fromUrn( "urn:samm:io.catenax.part_as_planned:2.0.0#PartAsPlanned" );
      final AspectModel aspectModel = new AspectModelLoader( FILE_SYSTEM_STRATEGY ).load( aspectUrn );
      final Aspect aspect = aspectModel.aspects().stream().filter( a -> a.urn().equals( aspectUrn ) ).findFirst().orElseThrow();

      final AspectModelDocumentationGenerator generator = new AspectModelDocumentationGenerator( aspect );
      generator.generate( htmlFileName -> {
         try {
            final File output = new File( System.getProperty( "user.dir" ) ).toPath()
                  .resolve( "target" )
                  .resolve( htmlFileName )
                  .toFile();
            return new FileOutputStream( output );
         } catch ( final FileNotFoundException e ) {
            throw new RuntimeException( e );
         }
      }, Map.of(), Locale.ENGLISH );
   }
}
