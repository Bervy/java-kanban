package ru.yandex.practicum.adapters;

/*
 * @author Vlad Osipov
 * @create 2022-07-10   15:01
 */

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.parse(jsonReader.nextString());
    }
}