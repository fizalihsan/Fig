package com.fig.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collection;

import static com.fig.util.BindingUtil.*;
import static junit.framework.Assert.assertEquals;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 1:40 PM
 */
public class BindingUtilTest {
    private static DomainA object = new DomainA("hello", 123);
    private static DomainB object2 = new DomainB("world", 234);
    private static String json = "{\"string\":\"hello\",\"integer\":123}";
    private static String json2 = "{\"hello\":\"world\",\"serialString\":\"world\",\"serialInteger\":234}";
    private static String prettyJson = "{\n" +
            "  \"string\": \"hello\",\n" +
            "  \"integer\": 123\n" +
            "}";
    private static String prettyJson2 = "{\n" +
            "  \"hello\": \"world\",\n" +
            "  \"serialString\": \"world\",\n" +
            "  \"serialInteger\": 234\n" +
            "}";

    @Test
    public void testToJson() throws Exception {
        assertEquals(json, toJson(object));
    }

    @Test
    public void testToJson2() throws Exception {
        assertEquals(json2, toJson(object2));
    }

    @Test
    public void testToPrettyJson() throws Exception {
        assertEquals(prettyJson, toPrettyJson(object));
    }

    @Test
    public void testToPrettyJson2() throws Exception {
        assertEquals(prettyJson2, toPrettyJson(object2));
    }

    @Test
    public void testFromJson() throws Exception {
        assertEquals(object.toString(), fromJson(json, DomainA.class).toString());
    }

    @Test
    public void testFromJsonArray() throws Exception {
        String json = "[{\"string\":\"hello\",\"integer\":123}, {\"string\":\"world\",\"integer\":234}]";
        final Collection<DomainA> collection = fromJsonArray(json, DomainA.class);
        assertEquals(2, collection.size());
    }

    /* -------------------------- Test Domain Classes -------------------------- */
    private static class DomainA{
        private String string;
        private int integer;

        private DomainA(String string, int integer) {
            this.string = string;
            this.integer = integer;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DomainA{");
            sb.append("string='").append(string).append('\'');
            sb.append(", integer=").append(integer);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class DomainB implements JsonSerializer<DomainB> {
        private String string;
        private int integer;

        private DomainB(String string, int integer) {
            this.string = string;
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public int getInteger() {
            return integer;
        }

        @Override
        public JsonElement serialize(DomainB src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("hello", "world");
            jsonObject.addProperty("serialString", src.getString());
            jsonObject.addProperty("serialInteger", src.getInteger());
            return jsonObject;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DomainB{");
            sb.append("string='").append(string).append('\'');
            sb.append(", integer=").append(integer);
            sb.append('}');
            return sb.toString();
        }
    }
}
