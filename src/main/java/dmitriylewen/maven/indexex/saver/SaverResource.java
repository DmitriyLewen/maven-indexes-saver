package dmitriylewen.maven.indexex.saver;

import org.apache.maven.index.reader.ResourceHandler;
import org.apache.maven.index.reader.resource.UrlResource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SaverResource implements ResourceHandler.Resource {
    private final UrlResource urlResource;

    public SaverResource(UrlResource urlResource) {
        this.urlResource = urlResource;
    }


    @Override
    public InputStream read() throws IOException {
        return new BufferedInputStream(urlResource.read());
    }
}
