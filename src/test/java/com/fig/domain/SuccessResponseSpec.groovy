package com.fig.domain

import spock.lang.Specification

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/23/13
 * Time: 7:05 PM
 */
class SuccessResponseSpec extends Specification{
    def "getRequestId"() {
        def response = Spy(SuccessResponse){
            getHostName() >> "hostname"
            getProcessId() >> "processid"
            getUniqueId() >> "uniqueid"
        }

        when:
        def requestId = response.getRequestId()
        then:
        requestId == "hostname-processid-uniqueid"
    }

}
