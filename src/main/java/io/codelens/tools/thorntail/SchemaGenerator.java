package io.codelens.tools.thorntail;

import io.codelens.tools.thorntail.io.CompactJsonWriter;
import org.reflections.Reflections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.codelens.tools.thorntail.Utils.*;

public class SchemaGenerator {

    private static final String FILENAME_PREFIX = "thorntail-schema-";
    private static final String NL = "\n";

    public static void main(String[] args) {
        if (args.length < 1) {
            err("Version parameter required");
            System.exit(1);
        }
        String version = args[0].trim();
        String outputDirectory = args.length > 1 ? args[1] : System.getProperty("user.dir");
        if (!Paths.get(outputDirectory).toFile().isDirectory()) {
            err("Specified output directory not exists or not a directory: " + outputDirectory);
            System.exit(2);
        }

        Reflections schemaModelBuilderReflections = new Reflections(ModelBuilder.class.getPackage().getName());
        
        out("");
        out("Thorntail schema generator");
        out("============================================================");
        printf("version: %s%n", version);
        printf("output directory: %s%n", outputDirectory);
        out("building schema model...");
        SchemaModel schemaModel = SchemaModel.of(schemaModelBuilderReflections.getSubTypesOf(ModelBuilder.class));
        
        out("writing schema files...");
        generateAndWriteSchemaFiles(schemaModel, version, outputDirectory);
        
        out("... done");
    }
    
    private static void generateAndWriteSchemaFiles(SchemaModel schemaModel, String version, String outputDirectory) {
        JsonObject schemaJsonObject = schemaModel.generateSchema(true);
        Properties schemaProperties = schemaModel.generateProperties(true);

        JsonWriterFactory writerFactory = createJsonWriterFactory();

        File jsonSchemaFile = new File(outputDirectory,  FILENAME_PREFIX + version + ".json");
        File compactJsonSchemaFile = new File(outputDirectory, FILENAME_PREFIX + version + "-compact.json");
        File propertiesFile = new File(outputDirectory, FILENAME_PREFIX + version + ".properties");

        try (JsonWriter jsonSchemaWriter = writerFactory.createWriter(new FileWriter(jsonSchemaFile));
             JsonWriter compactJsonSchemaWriter = writerFactory.createWriter(
                     new CompactJsonWriter(new FileWriter(compactJsonSchemaFile)));
             Writer propertiesWriter = new FileWriter(propertiesFile)) {

            jsonSchemaWriter.write(schemaJsonObject);
            compactJsonSchemaWriter.write(schemaJsonObject);
            writeSortedProperties(schemaProperties, propertiesWriter);
        } catch (IOException e) {
            err("Could not write output file: " + e.getMessage());
            System.exit(3);
        }
    }
    
    private static JsonWriterFactory createJsonWriterFactory() {
        Map<String, Object> writerProps = new HashMap<>();
        writerProps.put("javax.json.stream.JsonGenerator.prettyPrinting", Boolean.TRUE);
        return Json.createWriterFactory(writerProps);
    }

    private static void writeSortedProperties(Properties properties, Writer writer) throws IOException {
        writer.append("# Created by thorntail-json-schema-generator").append(NL);
        writer.append("# ").
                append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).
                append(NL).append(NL);

        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = new HashMap(properties);
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> writePropertiesEntry(writer, entry));
    }

    private static void writePropertiesEntry(Writer writer, Map.Entry<String, String> entry) {
        try {
            writer.append(replaceKeyToAsterisk(entry.getKey())).append(" = ").append(entry.getValue()).append(NL);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write line into writer", e);
        }
    }

    private static String replaceKeyToAsterisk(String key) {
        return key.replace(Node.DOT + Node.KEY + Node.DOT, Node.DOT + "*" + Node.DOT);
    }

}
