package com.shree.cloudweb.product;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton // Tells Micronaut to create this object once and share it
public class InMemoryProductRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryProductRepository.class);

    private static final int WORKER_BITS = 10;
    private static final int SEQUENCE_BITS = 12;
    private static final long SEQUENCE_MASK =
            (1L << SEQUENCE_BITS) - 1;
    private static final int WORKER_SHIFT = SEQUENCE_BITS;
    private static final int TIMESTAMP_SHIFT = WORKER_BITS + SEQUENCE_BITS;

    private static long lastTimestamp;
    private static int sequence;

    @Value("${app.file.path}")
    private String filePath;
    @Value("${app.file.name}")
    private String fileName;

    @Value("${app.worker.id:1}") // Default worker ID to 1 if not provided
    private long workerId;

    // DIRTY FLAG: Tracks if data has changed since the last background save
    private final AtomicBoolean isDirty = new AtomicBoolean(false);


    // The in-memory store: Keys are IDs (String), Values are Products
    private final Map<Long, Product> productStore = new ConcurrentHashMap<>();

    // Snowflake ID generation logic
    @SuppressFBWarnings(
            value = "SSD_DO_NOT_USE_INSTANCE_LOCK_ON_SHARED_STATIC_DATA",
            justification = "Micronaut manages this repository as a @Singleton bean; only one instance exists in production."
    )
    private synchronized long generateSnowflakeId() {
        // For simplicity, let's return a random UUID as a string
        //return java.util.UUID.randomUUID().toString();

        // Implement your Snowflake ID generation logic here
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = Math.toIntExact((sequence + 1) & SEQUENCE_MASK);

            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return composeId(timestamp, workerId, sequence);

    }

    // Wait until the next millisecond if the sequence overflows
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        try {
            while (timestamp <= lastTimestamp) {
                Thread.sleep(100);
                timestamp = System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return timestamp;
    }

    // Compose the final ID using bitwise operations
    private long composeId(long timestamp,
                           long workerId,
                           long sequence) {

        return (timestamp << TIMESTAMP_SHIFT)
                | (workerId << WORKER_SHIFT)
                | sequence;
    }

    // Method to add a product to the store
    public ProductStatus addProduct(Product product) {
        LOG.debug("add product {}", product);
        if (productStore.containsKey(product.id())) {
            return new ProductStatus("add", "Product ID already exists in data store.", null, null);
        } else {
            Product productWithNewId = new Product(generateSnowflakeId(), product.name(), product.description(), product.url(), product.price());
            productStore.put(productWithNewId.id(), productWithNewId);
            isDirty.set(true);
            return new ProductStatus("add", "success", productWithNewId, null);
        }
    }

    // Method to delete a product from the store by ID
    public ProductStatus deleteProduct(long id) {
        LOG.debug("delete product with id {}", id);
        if (!productStore.containsKey(id)) {
            return new ProductStatus("delete", "Product ID doesn't exist in data store.", null, null);
        } else {
            Product oldProduct = productStore.remove(id);
            isDirty.set(true);
            return new ProductStatus("delete", "success", null, oldProduct);
        }
    }

    // Method to update a product in the store by ID
    public ProductStatus updateProduct(long id, Product product) {
        LOG.debug("update product with id {} as {}", id, product);
        if(product.id() <= 0) {
            LOG.error("Product ID is invalid for update.");
            return new ProductStatus("update", "Invalid Data: Product ID is required for update.", null, null);
        } else if (!productStore.containsKey(id)) {
            return new ProductStatus("update", "Product ID doesn't exist in data store.", null, null);
        } else {
            Product oldProduct = productStore.put(id, product);
            isDirty.set(true);
            return new ProductStatus("update", "success", product, oldProduct);
        }
    }

    // Method to retrieve a product store in case you want to access it directly (not recommended for production)
    public Map<Long, Product> getProductStore() {
        LOG.debug("get product store");
        return Map.copyOf(productStore); // Return an unmodifiable copy to prevent external modifications
    }


    // Validate that the file path and name are set and not empty
    public boolean validateFilePathAndName() {
        boolean valid = true;
        if (filePath == null || filePath.isEmpty()) {
            valid = false;
            LOG.info("File path is not set. Please provide a valid file path.");
        } else {
            LOG.info("File path exists: {}", filePath);
        }

        if (fileName == null || fileName.isEmpty()) {
            valid = false;
            LOG.info("File name is not set. Please provide a valid file name.");
        } else {
            LOG.info("File name is set: {}", fileName);
        }
        return valid;
    }

    /**
     * LOAD: Read the raw binary file back into a Java Map.
     */
    @SuppressFBWarnings(
            value = {
                    "PATH_TRAVERSAL_IN",
                    "OBJECT_DESERIALIZATION"
            },
            justification = "Path is validated and deserialization is protected by ObjectInputFilter."
    )
    @PostConstruct
    public void loadDataFromFile() {

        Path absolutePath = Paths.get(filePath + File.separator + fileName).toAbsolutePath();
        Path parentDir = absolutePath.getParent();

        LOG.info("Checking strict persistence target: {}", absolutePath);

        if (!validateFilePathAndName()) {
            throw new IllegalStateException(
                    "[STARTUP FAILURE] Invalid file path or name. Please check your configuration."
            );
        }

        if (!Files.exists(parentDir)) {
            LOG.info("Required persistence directory does not exist: {}. Creating it now.", parentDir);
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "[STARTUP FAILURE] Failed to create required persistence directory: " + parentDir, e
                );
            }
        }

        // 1. CRITICAL: Fail immediately if the file does not exist on disk
        if (!Files.exists(absolutePath)) {

            LOG.info("Required persistence file does not exist: {}. Application cannot start without an initial database seed file.", absolutePath);
            LOG.info("Creating missing persistence file: {}", absolutePath);
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "[STARTUP FAILURE] Required persistence file does not exist: " + absolutePath +
                                ". Application cannot start without an initial database seed file."
                );
            }
        }

        // 2. Fail if it exists but is actually a folder by mistake
        if (Files.isDirectory(absolutePath)) {
            throw new IllegalStateException(
                    "[STARTUP FAILURE] Path points to a directory, but a file is required: " + absolutePath
            );
        }

        // 3. Fail if the file exists but lacks operational permissions
        if (!Files.isReadable(absolutePath)) {
            throw new IllegalStateException(
                    "[STARTUP FAILURE] Persistence file exists but lacks READ permissions: " + absolutePath
            );
        }

        if (!Files.isWritable(absolutePath)) {
            throw new IllegalStateException(
                    "[STARTUP FAILURE] Persistence file exists but lacks WRITE permissions: " + absolutePath
            );
        }

        // 4. If all guards pass, safely proceed to hydrate your memory maps
        LOG.info("All strict path and file validations passed. Hydrating database...");

        // Using try-with-resources cleanly closes streams automatically when finished
        try (FileInputStream fis = new FileInputStream(absolutePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {


            // Create a strict whitelist configuration
            // "com.shree.cloudweb.product.Product;java.util.ArrayList;java.lang.*;!*"
            ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
                    "java.util.HashMap;" +
                            "java.util.Map$Entry;" +
                            "java.lang.Long;" +
                            "java.lang.Number;" +
                            "com.shree.cloudweb.product.Product;" + // Your product record package/classname
                            "!*" // REJECT EVERYTHING ELSE (!*)
            );


            /* Helped in learning the filtering process, but not needed in production
            ObjectInputFilter filter = info -> {

                LOG.info("Class      : {}", info.serialClass());
                LOG.info("Array Length: {}", info.arrayLength());
                LOG.info("Depth      : {}", info.depth());
                LOG.info("References : {}", info.references());
                LOG.info("Bytes Read : {}", info.streamBytes());

                return ObjectInputFilter.Status.UNDECIDED;
            };*/

            // Apply the filter to the stream before reading objects
            ois.setObjectInputFilter(filter);

            // Read the entire map block directly out of the file stream
            Map<Long, Product> loadedData = (Map<Long, Product>) ois.readObject();

            productStore.putAll(loadedData);
            LOG.info("Successfully restored {} products natively from disk.", productStore.size());

        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Failed to natively load product data file. Starting empty.", e);
        }
    }

    /**
     * WRITE: Flush the live Map as raw bytes down to the file system.
     */
    @SuppressFBWarnings
    private synchronized void writeDataToFile() {
        File dataFile = new File(filePath + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(dataFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Serialize and write the entire map state instantly
            oos.writeObject(productStore);
            LOG.debug("Product cache successfully flushed natively to disk.");

        } catch (IOException e) {
            LOG.error("Critical error: Failed to save changes natively to file!", e);
        }
    }

    /**
     * PERIODIC WRITE: Runs in the background every 5 seconds.
     * Micronaut automatically executes this on a dedicated scheduling thread pool.
     */
    @Scheduled(fixedRate = "5s")
    public void periodicSave() {
        // Only touch the hard drive if data has actually changed!
        if (isDirty.compareAndSet(true, false)) {
            LOG.debug("Changes detected. Flushing in-memory snapshot to disk...");
            executeWrite();
        }
    }

    /**
     * 2. The Shutdown Safety Net: Forces a write on shutdown
     * NO MATTER WHAT, ignoring the dirty flag to guarantee safety.
     */
    @PreDestroy
    public void shutdownSave() {
        LOG.info("Shutdown intercepted. Forcing final data synchronization...");
        executeWrite();
    }

    /**
     * 3. The Actual Core Logic: A single, clean, synchronized method
     * that handles the heavy lifting for both.
     */
    @SuppressFBWarnings(
            value = "PATH_TRAVERSAL_IN",
    justification = "Path is retrieved from a secure, internal property file"
            )
    private synchronized void executeWrite() {
        // 1. Take a rapid copy of the current data state to minimize locking time
        Map<Long, Product> snapshot = new HashMap<>(productStore);

        File dataFile = new File(filePath + File.separator + fileName);

        // 2. Write the snapshot out to a temporary file first to prevent corruption
        File tempFile = new File(dataFile.getAbsolutePath() + ".tmp");

        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Serialize the entire cloned map structure as a single object block
            oos.writeObject(snapshot);
            oos.flush();

            // Close the stream before swapping handles
            oos.close();

            if (!tempFile.exists()) {
                LOG.error("Temporary file was not created successfully. Aborting save operation.");
                isDirty.set(true); // Re-mark dirty to try again next cycle
                return;
            } else {
                if (cleanAtomicFileReplacement(tempFile, dataFile)) {
                    LOG.info("Successfully flushed in-memory snapshot to disk: {}", dataFile.getAbsolutePath());
                } else {
                    LOG.error("Failed to replace old data file with new snapshot. Aborting save operation.");
                    isDirty.set(true); // Re-mark dirty to try again next cycle
                }
            }


        } catch (IOException e) {
            LOG.error("Error writing background binary snapshot to file", e);
            isDirty.set(true); // Re-mark dirty to try again next cycle
        }
    }


    // Clean OS-level atomic file replacement
    private boolean cleanAtomicFileReplacement(File tempFile, File dataFile) {
        boolean result = false;
        if (tempFile.exists()) {
            if (dataFile.exists()) {
                if (!dataFile.delete()) {
                    LOG.error("Failed to delete old data file during cleanup: {}", dataFile.getAbsolutePath());
                }
            }
            if (!tempFile.renameTo(dataFile)) {
                LOG.error("Failed to rename temporary file to final data file during cleanup: {} -> {}", tempFile.getAbsolutePath(), dataFile.getAbsolutePath());
            } else {
                LOG.info("Successfully renamed temporary file to final data file: {} -> {}", tempFile.getAbsolutePath(), dataFile.getAbsolutePath());
                result = true;
            }
        }
        return result;
    }

}