package com.fig.messaging;

import com.fig.config.ActiveMQConfig;
import com.fig.config.FigConfiguration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;

/**
 * Generic utility to interact with ActiveMQ messaging
 * User: Fizal
 * Date: 12/9/13
 * Time: 6:31 PM
 */
public class MessagingUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingUtil.class);

    private BrokerService broker;
    private QueueSession queueSession;
    private QueueConnection queueConnection;
    private QueueSender queueSender;
    private QueueReceiver queueReceiver;
    private Queue queue;
    private static final MessagingUtil INSTANCE = new MessagingUtil();

    private MessagingUtil(){

    }

    public static MessagingUtil getInstance(){
        if(INSTANCE.broker == null){
            INSTANCE.startBroker();
            INSTANCE.startConnection();
        }
        return INSTANCE;
    }

    @VisibleForTesting
    void startBroker(){

        ActiveMQConfig config = FigConfiguration.getInstance().getActiveMQConfig();
        try {
            final BrokerService brokerService = new BrokerService();

            //TODO connector types, plugins, security
            //Add transport connector
            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI(config.getBrokerURI()));
            brokerService.setBrokerName(config.getBrokerName()); //local JVMs can connect as vm://fig
            brokerService.addConnector(connector);
            brokerService.setDataDirectory(config.getDataFolderLocation());

            brokerService.setDestinationPolicy(getPolicyMap());

            Runnable shutdownHook = new Runnable() {
                @Override
                public void run() {
                    LOG.info("Shutting down...");
                    shutdown();
                }
            };
            brokerService.setShutdownHooks(Arrays.asList(shutdownHook));

            brokerService.start();
            brokerService.waitUntilStarted();
            LOG.info("ActiveMQ embedded broker started successfully...");
            this.broker = brokerService;
        } catch (Exception e) {
            throw new RuntimeException("Error starting ActiveMQ Message Broker", e);
        }
    }

    /**
     * Initializes
     */
    @VisibleForTesting
    void startConnection()  {
        LOG.info("Initializing ActiveMQ connection factory...");
        ActiveMQConfig config = FigConfiguration.getInstance().getActiveMQConfig();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(broker.getVmConnectorURI());
        try {
            this.queueConnection = connectionFactory.createQueueConnection();
            /*queueConnection.setExceptionListener(new javax.jms.ExceptionListener() {
                @Override
                public void onException(JMSException exception) {
                    LOG.error("JMS Exception received: ", exception);
                }
            });*/

            final boolean transacted = false;
            this.queueSession = this.queueConnection.createQueueSession(transacted, Session.AUTO_ACKNOWLEDGE);
            this.queue = this.queueSession.createQueue(config.getRequestQueue());

            this.queueSender = this.queueSession.createSender(this.queue);
            this.queueReceiver = this.queueSession.createReceiver(this.queue);
            this.queueConnection.start();
            LOG.info("ActiveMQ connection started successfully...");
        } catch (Exception e) {
            throw new RuntimeException("Error creating or connecting to queue: " + config.getRequestQueue(), e);
        }
    }

    @VisibleForTesting
    PolicyMap getPolicyMap(){
        PolicyMap policyMap = new PolicyMap();
        PolicyEntry policy = new PolicyEntry();
        policy.setConsumersBeforeDispatchStarts(2);
        policy.setTimeBeforeDispatchStarts(1000);
        policyMap.setDefaultEntry(policy);
        return policyMap;
    }

    /**
     * Set a message listener on the queue. Only 1 message listener can be set on a queue. If the method is invoked multiple
     * times, then only the last listener is retained.
     * @param messageListener
     */
    public void setQueueListener(MessageListener messageListener) {
        try {
            this.queueReceiver.setMessageListener(messageListener);
        } catch (JMSException e) {
            throw new RuntimeException("Error setting listener on request queue", e);
        }
    }

    /**
     * Publish a serializable object to the EMS queue
     * @param object
     */
    public void send(Serializable object){
        try {
            Message msg;
            if(object instanceof String){
                msg = this.queueSession.createTextMessage();
                ((TextMessage)msg).setText((String) object);
            } else {
                msg = this.queueSession.createObjectMessage();
                ((ObjectMessage)msg).setObject(object);
            }
            this.queueSender.send(msg); //TODO how expensive is it to create a queue sender for every request. or is it threadsafe
        } catch (JMSException e) {
            throw new RuntimeException("Error sending text message to queue", e);
        }
    }

    /**
     * Close the queue connection and session and stops the broker.
     */
    public void shutdown(){
        try {
            this.queueConnection.stop();
        } catch (JMSException e) {
            LOG.error("Error stopping ActiveMQ queue connection.", e);
        }

        try {
            this.queueSession.close();
        } catch (JMSException e) {
            LOG.error("Error closing ActiveMQ queue session.", e);
        }

        if(this.broker.isStarted()){
            try {
                this.broker.stop();
                this.broker.waitUntilStopped();
            } catch (Exception e) {
                LOG.error("Error stopping ActiveMQ broker.", e);
            }
        }
    }

    @VisibleForTesting
    BrokerService getBroker() {
        return broker;
    }
}
