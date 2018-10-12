package sample.storage;

import ch.qos.logback.core.util.FileUtil;
import com.microsoft.azure.storage.blob.BlobRange;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.TransferManager;
import com.microsoft.azure.storage.blob.models.ContainerCreateResponse;
import com.microsoft.rest.v2.RestException;
import com.microsoft.rest.v2.util.FlowableUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    public static void uploadFile(BlockBlobURL blob, File sourceFile) throws IOException {
        LOGGER.info("Start uploading file {}...", sourceFile);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());

        TransferManager.uploadFileToBlockBlob(fileChannel, blob, 8*1024*1024, null)
                .subscribe(response -> {
                    LOGGER.info("File {} is uploaded, status code: {}.", sourceFile.toPath(),
                            response.response().statusCode());
                }, error -> {
                    LOGGER.error("Failed to upload file {} with error {}.", sourceFile.toPath(), error.getMessage());
                });
    }

    public static void deleteBlob(BlockBlobURL blockBlobURL) {
        LOGGER.info("Start deleting file {}...", blockBlobURL.toURL());
        blockBlobURL.delete(null, null, null)
                .subscribe(
                        response -> LOGGER.info("Blob {} is deleted.", blockBlobURL.toURL()),
                        error -> LOGGER.error("Failed to delete blob {} with error {}.",
                                blockBlobURL.toURL(), error.getMessage()));
    }

    public static void downloadBlob(BlockBlobURL blockBlobURL, File downloadToFile) {
        LOGGER.info("Start downloading file {} to {}...", blockBlobURL.toURL(), downloadToFile);
        FileUtils.deleteQuietly(downloadToFile);

        blockBlobURL.download(new BlobRange().withOffset(0).withCount(4*1024*1024L), null, false, null)
                .flatMapCompletable(
                        response -> { AsynchronousFileChannel channel = AsynchronousFileChannel
                                .open(Paths.get(downloadToFile.getAbsolutePath()), StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE);
                    return FlowableUtil.writeFile(response.body(null), channel);
                }).doOnComplete(() -> LOGGER.info("File is downloaded to {}.", downloadToFile))
                .subscribe();
    }

    public static void createContainer(ContainerURL containerURL, String containerName) {
        LOGGER.info("Start creating container {}...", containerName);
        try {
            ContainerCreateResponse response = containerURL.create(null, null, null).blockingGet();
            LOGGER.info("Storage container {} created with status code: {}.", containerName, response.statusCode());
        } catch (RestException e) {
            if (e instanceof RestException && e.response().statusCode() != 409) {
                LOGGER.error("Failed to create container {}.", containerName, e);
                throw e;
            } else {
                LOGGER.info("{} container already exists.", containerName);
            }
        }
    }
}
