package com.fig.messaging

import org.apache.activemq.broker.region.policy.PolicyEntry
import org.apache.activemq.broker.region.policy.PolicyMap
import spock.lang.Shared
import spock.lang.Specification

import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage

/**
 * Comment here about the class
 * User: Fizal
 * Date: 12/10/13
 * Time: 6:18 PM
 */
class MessagingUtilSpec extends Specification {

    @Shared MessagingUtil util

    def setupSpec(){
        util = MessagingUtil.getInstance()
        sleep(5000) //give some room for messages to flow
    }

    def "MessagingUtil - Send and receive message"() {
        given:
        MessagingUtil util = MessagingUtil.getInstance()

        def messageText;
        util.setRequestQueueListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if(message instanceof TextMessage){
                    messageText = ((TextMessage)message).text
                }
            }
        });

        when: util.send("Hello World...")
        then: messageText == "Hello World..."
    }

    def "Test PolicyMap"() {
        when:
        PolicyMap policyMap = util.getPolicyMap()
        PolicyEntry entry = policyMap.getDefaultEntry()
        then:
        entry.getConsumersBeforeDispatchStarts() == 2
        entry.getTimeBeforeDispatchStarts() == 1000
    }

    void cleanupSpec() {
        util.shutdown()
    }
}
