package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import utility.Status;

public class StatusAdapter extends TypeAdapter<Status> {

    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        if (status == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(status.name());
        }
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            return null;
        }
        return Status.valueOf(jsonReader.nextString());
    }
}
