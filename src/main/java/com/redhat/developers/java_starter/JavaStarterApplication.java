package com.redhat.developers.java_starter;

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;

import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tech.amikos.chromadb.Client;
import tech.amikos.chromadb.Collection;
import tech.amikos.chromadb.EmbeddingFunction;
import tech.amikos.chromadb.embeddings.ollama.OllamaEmbeddingFunction;

@SpringBootApplication
public class JavaStarterApplication implements InitializingBean {

	private static final String OLLAMA_EMBEDDING_URL = "http://ollama-embedding:8080/api/embed";
    private static final String CHROMADB_URL = "http://chroma:8080";


	public static void main(String[] args) {
		SpringApplication.run(JavaStarterApplication.class, args);
	}

	@Override
    public void afterPropertiesSet() {
        setup();
    }

	// Method to fetch data and split into chunks
    private void setup() {
        try {
            
            // Create a collection within the vector DB.
            // This is a mandatory step.
            Client chromaDbClient = new Client(CHROMADB_URL);
            chromaDbClient.setTimeout(300);
            
            EmbeddingFunction ef = new OllamaEmbeddingFunction(new URL(OLLAMA_EMBEDDING_URL),
                    "nomic-embed-text");
            Collection collection = chromaDbClient.createCollection("test-collection", null, true, ef);

            
            // Let's add some text to the vector DB!
            // `documents` is an array of text chunks
            // `ids` is an array of unique IDs for each document.
            // For our simple purposes, we can use random UUIDs as the `ids`.

            collection.add(null, null, Arrays.asList("In space, nobody can hear you scream because space is a vacuum, meaning it has no air or other medium to carry sound waves. Sound waves require a medium like air, water, or solid objects to travel through. In the vacuum of space, there is no such medium, so sound cannot propagate. As a result, any sound you make, including a scream, would not be heard by anyone else."), Arrays.asList(UUID.randomUUID().toString()));
            
            // Now let's query the DB!
            // We'll just print out the result to the console so we can see that the
            // database and embedding model are working.

            Collection.QueryResponse qr = collection.query(Arrays.asList("Why can nobody hear you scream in space?"), 3, null, null, null);
            System.out.println(qr);
            
            // Add to collection if needed (embed and add to database)
            // This part depends on your specific embedding service.
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// REST endpoint for POST requests
    @PostMapping(value = "/", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> hello(@RequestBody String body) {
        try {
            
            // Do something here.

            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
