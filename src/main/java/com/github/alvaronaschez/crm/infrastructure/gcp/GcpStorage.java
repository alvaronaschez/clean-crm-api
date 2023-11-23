package com.github.alvaronaschez.crm.infrastructure.gcp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GcpStorage {
    /**
     * https://github.com/fsouza/fake-gcs-server/tree/main/examples/java
     * https://cloud.google.com/storage/docs/uploading-objects-from-memory
     * https://cloud.google.com/storage/docs/uploading-objects#permissions-client-libraries
     */

    private static Storage getStorage(String projectId) {
        return StorageOptions.newBuilder()
                .setHost("http://localhost:4443")
                .setProjectId(projectId)
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
    }

    public static String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        uploadFromMemory("project-id", "sample-bucket", fileName, file.getBytes());
        var template = "http://localhost:4443/storage/v1/b/sample-bucket/o/%s?alt=media";
        return String.format(template, fileName);
    }

    public static void uploadFromMemory(
            String projectId, String bucketName, String objectName, byte[] contents) throws IOException {
        Storage storage = getStorage(projectId);
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        // byte[] content = contents.getBytes(StandardCharsets.UTF_8);
        storage.createFrom(blobInfo, new ByteArrayInputStream(contents));
    }

    // public static void uploadObjectFromMemory(
    // String projectId, String bucketName, String objectName, String contents)
    // throws IOException {
    // /**
    // * https://cloud.google.com/storage/docs/uploading-objects-from-memory
    // */
    // // The ID of your GCP project
    // // String projectId = "your-project-id";

    // // The ID of your GCS bucket
    // // String bucketName = "your-unique-bucket-name";

    // // The ID of your GCS object
    // // String objectName = "your-object-name";

    // // The string of contents you wish to upload
    // // String contents = "Hello world!";

    // Storage storage = getStorage(projectId);
    // BlobId blobId = BlobId.of(bucketName, objectName);
    // BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    // byte[] content = contents.getBytes(StandardCharsets.UTF_8);
    // storage.createFrom(blobInfo, new ByteArrayInputStream(content));

    // System.out.println(
    // "Object "
    // + objectName
    // + " uploaded to bucket "
    // + bucketName
    // + " with contents "
    // + contents);
    // }

    // public static void uploadObjectFromDisk(
    // String projectId, String bucketName, String objectName, String filePath)
    // throws IOException {
    // /**
    // *
    // https://cloud.google.com/storage/docs/uploading-objects#permissions-client-libraries
    // */
    // // The ID of your GCP project
    // // String projectId = "your-project-id";

    // // The ID of your GCS bucket
    // // String bucketName = "your-unique-bucket-name";

    // // The ID of your GCS object
    // // String objectName = "your-object-name";

    // // The path to your file to upload
    // // String filePath = "path/to/your/file"

    // Storage storage = getStorage(projectId);
    // BlobId blobId = BlobId.of(bucketName, objectName);
    // BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // // Optional: set a generation-match precondition to avoid potential race
    // // conditions and data corruptions. The request returns a 412 error if the
    // // preconditions are not met.
    // Storage.BlobWriteOption precondition;
    // if (storage.get(bucketName, objectName) == null) {
    // // For a target object that does not yet exist, set the DoesNotExist
    // // precondition.
    // // This will cause the request to fail if the object is created before the
    // // request runs.
    // precondition = Storage.BlobWriteOption.doesNotExist();
    // } else {
    // // If the destination already exists in your bucket, instead set a
    // // generation-match
    // // precondition. This will cause the request to fail if the existing object's
    // // generation
    // // changes before the request runs.
    // precondition = Storage.BlobWriteOption.generationMatch(
    // storage.get(bucketName, objectName).getGeneration());
    // }
    // storage.createFrom(blobInfo, Paths.get(filePath), precondition);

    // System.out.println(
    // "File " + filePath + " uploaded to bucket " + bucketName + " as " +
    // objectName);
    // }
}
