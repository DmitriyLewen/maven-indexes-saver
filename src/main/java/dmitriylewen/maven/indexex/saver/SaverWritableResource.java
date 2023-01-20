package dmitriylewen.maven.indexex.saver;

import org.apache.maven.index.reader.WritableResourceHandler;
import org.apache.maven.index.reader.resource.UrlResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaverWritableResource implements WritableResourceHandler.WritableResource {
    private final UrlResource urlResource;

    public SaverWritableResource(UrlResource urlResource) {
        this.urlResource = urlResource;
    }


    @Override
    public OutputStream write() {
        return null; // This stream is not used
    }

    @Override
    public InputStream read() throws IOException {
        InputStream inputStream = urlResource.read();
        if (inputStream == null){
            throw new FileNotFoundException();
        }
        return urlResource.read();
    }
}
