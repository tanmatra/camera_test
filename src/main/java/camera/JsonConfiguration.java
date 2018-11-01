package camera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

class JsonConfiguration implements Configuration
{
    private final File file;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JsonObject root;

    JsonConfiguration(String fileName) {
        file = new File(fileName);
        load();
    }

    private void load() {
        if (!file.exists()) {
            root = new JsonObject();
            return;
        }
        try (final InputStream inputStream = new FileInputStream(file);
             final Reader reader = new InputStreamReader(inputStream))
        {
            final JsonParser parser = new JsonParser();
            root = parser.parse(reader).getAsJsonObject();
        } catch (Exception e) {
            System.out.println("Error loading configuration: " + e.toString());
            root = new JsonObject();
        }
    }

    private void save() {
        try (final OutputStream outputStream = new FileOutputStream(file);
             final OutputStreamWriter writer = new OutputStreamWriter(outputStream))
        {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.toString());
        }
    }

    private JsonObject getCameraObject(int number) {
        final String key = "camera" + number;
        JsonObject object = root.getAsJsonObject(key);
        if (object == null) {
            object = new JsonObject();
            root.add(key, object);
        }
        return object;
    }

    @Override
    public String getCameraURI(int number) {
        final JsonObject cameraObject = getCameraObject(number);
        final JsonPrimitive primitive = cameraObject.getAsJsonPrimitive("uri");
        return (primitive != null) ? primitive.getAsString() : null;
    }

    @Override
    public void setCameraURI(int number, String uri) {
        final JsonObject cameraObject = getCameraObject(number);
        cameraObject.addProperty("uri", uri);
        save();
    }
}
