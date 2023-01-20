package dmitriylewen.maven.indexex.saver;

import org.apache.maven.index.reader.ResourceHandler;
import org.apache.maven.index.reader.resource.UrlResource;

public class SaverResourceHandler implements ResourceHandler {
    private final UrlResource urlResource;

    public SaverResourceHandler(UrlResource urlResource) {
        this.urlResource = urlResource;
    }

    @Override
    public Resource locate(String s) {
        return new SaverResource(urlResource);
    }
}
