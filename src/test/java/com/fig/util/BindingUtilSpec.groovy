package com.fig.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import spock.lang.Specification

import java.lang.reflect.Type

import static com.fig.util.BindingUtil.*
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/22/13
 * Time: 7:51 PM
 */
class BindingUtilSpec extends Specification {

    private static DomainA object1 = new DomainA("hello", 123);
    private static DomainB object2 = new DomainB("world", 234);
    private static String json1 = "{\"string\":\"hello\",\"integer\":123}";
    private static String json2 = "{\"hello\":\"world\",\"serialString\":\"world\",\"serialInteger\":234}";
    private static String prettyJson1 = "{\n" +
                                        "  \"string\": \"hello\",\n" +
                                        "  \"integer\": 123\n" +
                                        "}";
    private static String prettyJson2 = "{\n" +
                                        "  \"hello\": \"world\",\n" +
                                        "  \"serialString\": \"world\",\n" +
                                        "  \"serialInteger\": 234\n" +
                                        "}";

    def "Java Object to Json"(){
        expect:
        toJson(obj) == json

        where:
        obj     | json
        object1 | json1
        object2 | json2
    }

    def "Java Object to pretty Json"(){
        expect:
        toPrettyJson(obj) == json

        where:
        obj << [object1, object2]
        json << [prettyJson1, prettyJson2]
    }

    def "Json string to Java object"() {
        expect:
        fromJson(json, clazz).toString() == obj.toString()
        where:
        json  | obj     | clazz
        json1 | object1 | DomainA.class
    }

    def "Json array to Java objects"(){
        expect:
        fromJsonArray(json, clazz).size() == size
        where:
        json                                                                               | clazz         | size
        "[{\"string\":\"hello\",\"integer\":123}, {\"string\":\"world\",\"integer\":234}]" | DomainA.class | 2
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