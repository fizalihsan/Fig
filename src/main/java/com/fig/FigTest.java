package com.fig;

import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 4:17 PM
 */
public class FigTest {
    public static void main(String[] args) {
//        toJson();

        System.out.println(new Gson().toJson(new SuccessResponse("Done....")));
    }


    private static void toJson(){
        Task task = new Task();
        task.setName("Task1");
        final Map<String, String> payload = new HashMap<>();
        payload.put("key1", "value1");
        payload.put("key2", "value2");
        payload.put("key3", "value3");
        payload.put("key4", "value4");
        task.setPayload(payload);

        final Gson gson = new Gson();
        String json = gson.toJson(task);
        System.out.println(json);

        json = "\n" +
                "[\n" +
                "\t{\n" +
                "\t\t\"name\":\"Task1\",\n" +
                "\t\t\"dependsOn\": [],\n" +
                "\t\t\"payload\":{\n" +
                "\t\t\t\"key4\":\"value4\",\n" +
                "\t\t\t\"key3\":\"value3\",\n" +
                "\t\t\t\"key2\":\"value2\",\n" +
                "\t\t\t\"key1\":\"value1\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\":\"Task2\",\n" +
                "\t\t\"dependsOn\": [\"Task1\"],\n" +
                "\t\t\"payload\":{\n" +
                "\t\t\t\"key4\":\"value4\",\n" +
                "\t\t\t\"key3\":\"value3\",\n" +
                "\t\t\t\"key2\":\"value2\",\n" +
                "\t\t\t\"key1\":\"value1\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "]";

        Type collectionType = new TypeToken<Collection<Task>>(){}.getType();
        final Collection<Task> tasks = gson.fromJson(json, collectionType);
        System.out.println(tasks);

        System.out.println("JSON: \n\n" + gson.toJson(tasks));
    }
}
