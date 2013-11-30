package com.fig.webservices.validators
import com.fig.domain.ValidationResponse
import spock.lang.Specification

import static javax.ws.rs.core.Response.Status.BAD_REQUEST
import static javax.ws.rs.core.Response.Status.OK
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/29/13
 * Time: 5:36 PM
 */
class TaskCreateRequestValidatorSpec extends Specification {

    def "create"(){
        def validator = new TaskCreateRequestValidator()

        expect:
        ValidationResponse response = validator.valueOf(input)
        response.getResponse().getStatus() == status.getStatusCode()
        response.getResponse().getEntity().toString().endsWith(jsonMessage)
        tags.each { tag -> response.getResponse().getEntity().toString().contains(tag) }

        where:
        input| status      | tags                  | jsonMessage
        null | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing or empty.","message":"Request to create task(s) failed !!!"}"""
        ""   | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing or empty.","message":"Request to create task(s) failed !!!"}"""
        "a"  | BAD_REQUEST | ["reason", "message"] | """{"reason":"com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 1","message":"Invalid JSON sent in request"}"""
        """[{"name":"a1"}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1"}, {"name":"b1"}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1","key2":"value2"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1"}, "dependsOn":["b1","c1"]}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "dependsOn":["b1","c1"], "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"dependsOn":["b1","c1"], "properties":{"key1":"value1", "name":"a1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"dependsOn":["b1","c1"], "name":"a1", "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"dependsOn":["b1","c1"], "name":"a1", "properties":{"key1":null}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
    }

}
