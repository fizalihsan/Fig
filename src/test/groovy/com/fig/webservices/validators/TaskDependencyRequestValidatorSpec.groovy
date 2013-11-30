package com.fig.webservices.validators
import com.fig.domain.ValidationResponse
import spock.lang.Specification

import static javax.ws.rs.core.Response.Status.*
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 4:22 PM
 */
class TaskDependencyRequestValidatorSpec extends Specification {
    def "create"(){
        def validator = new TaskDependencyRequestValidator()

        expect:
        ValidationResponse response = validator.valueOf(input)
        response.getResponse().getStatus() == status.getStatusCode()
        response.getResponse().getEntity().toString().endsWith(jsonMessage)
        tags.each { tag -> response.getResponse().getEntity().toString().contains(tag) }

        where:
        input| status      | tags                  | jsonMessage
        null | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing or empty.","message":"Request to create/delete task(s) failed !!!"}"""
        ""   | BAD_REQUEST | ["reason", "message"] | """{"reason":"Property \\u0027request\\u0027 is missing or empty.","message":"Request to create/delete task(s) failed !!!"}"""
        "a"  | BAD_REQUEST | ["reason", "message"] | """{"reason":"com.fig.exception.JsonSyntaxException: com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 1","message":"Invalid JSON sent in request"}"""
        """[{"fromTask":"d1","toTasks":[]}]""" | OK | ["fromTask", "toTasks"] | """"message":"Request accepted successfully. "}"""
        """[{"fromTask":"d1","toTasks":["a1"]}]""" | OK | ["fromTask", "toTasks"] | """"message":"Request accepted successfully. "}"""
        """[{"fromTask":"d1","toTasks":["a1", "b1"]}]""" | OK | ["fromTask", "toTasks"] | """"message":"Request accepted successfully. "}"""
    }
}
