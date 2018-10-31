package camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class PropertiesConfiguration implements Configuration
{
    private final Properties properties = new Properties();

    private final File file;

    PropertiesConfiguration(String fileName) {
        file = new File(fileName);
        load();
    }

    private void load() {
        if (!file.exists()) {
            return;
        }
        try (final InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (final OutputStream outputStream = new FileOutputStream(file)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String uriKey(int number) {
        return "camera." + number + ".uri";
    }

    @Override
    public String getCameraURI(int number) {
        return properties.getProperty(uriKey(number));
    }

    @Override
    public void setCameraURI(int number, String uri) {
        properties.setProperty(uriKey(number), uri);
        save();
    }
}
