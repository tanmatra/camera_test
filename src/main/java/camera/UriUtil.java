package camera;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

class UriUtil
{
    private UriUtil() { }

    static File uriToFile(String uriString) {
        if (uriString == null) {
            return null;
        }
        final URI fileURI;
        try {
            fileURI = new URI(uriString);
        } catch (URISyntaxException e) {
            return null;
        }
        final File file;
        try {
            file = new File(fileURI);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return file;
    }
}
