package com.fig.webservices.validators

import com.fig.domain.ValidationResponse
import spock.lang.Specification

import static javax.ws.rs.core.Response.Status.BAD_REQUEST
import static javax.ws.rs.core.Response.Status.OK
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/28/13
 * Time: 6:37 PM
 */
class TaskUpdateRequestValidatorSpec extends Specification {

    def "update"(){
        def validator = new TaskUpdateRequestValidator()

        expect:
        ValidationResponse response = validator.valueOf(input)
        response.getResponse().getStatus() == status.getStatusCode()
        response.getResponse().getEntity().toString().endsWith(jsonMessage)
        tags.each { tag -> response.getResponse().getEntity().toString().contains(tag) }

        where:
        input| status      | tags                  | jsonMessage
        null | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing.","message":"Request to update task(s) failed !!!"}"""
        ""   | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing.","message":"Request to update task(s) failed !!!"}"""
        "a"  | BAD_REQUEST | ["reason", "message"] | """{"reason":"com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 1","message":"Invalid JSON sent in request"}"""
        """[{"name":"a1"}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1"}, {"name":"b1"}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1","key2":"value2"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "properties":{"key1":"value1"}, "dependsOn":["b1","c1"]}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"name":"a1", "dependsOn":["b1","c1"], "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"dependsOn":["b1","c1"], "properties":{"key1":"value1", "name":"a1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
        """[{"dependsOn":["b1","c1"], "name":"a1", "properties":{"key1":"value1"}}]""" | OK | ["requestId", "requestedTime", "message"] | """"message":"Request accepted successfully. "}"""
    }
}
