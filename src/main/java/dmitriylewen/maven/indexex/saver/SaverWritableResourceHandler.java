package dmitriylewen.maven.indexex.saver;

import org.apache.maven.index.reader.WritableResourceHandler;
import org.apache.maven.index.reader.resource.UrlResource;

public class SaverWritableResourceHandler implements WritableResourceHandler {
    private final SaverWritableResource saverWritableResource;

    public SaverWritableResourceHandler(UrlResource urlResource) {
        this.saverWritableResource = new SaverWritableResource(urlResource);
    }


    @Override
    public WritableResource locate(String s) {
        return saverWritableResource;
    }
}
