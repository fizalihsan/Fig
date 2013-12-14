package com.fig.messaging;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.ServiceRequest;
import com.fig.domain.ServiceRequestType;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * All the service requests sent via REST clients are funnelled through an EMS queue for serialization purposes. This class consumes all those requests
 * serially and invokes the respective processor class to process the request.
 * User: Fizal
 * Date: 12/9/13
 * Time: 10:46 PM
 */
@ThreadSafe
public class ServiceRequestConsumer implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRequestConsumer.class);

    @Override
    public void onMessage(Message message) {
        //TODO add transactional logic here with txn manager

        if (message instanceof ObjectMessage) {
            ServiceRequest serviceRequest = null;
            try {
                ActiveMQObjectMessage msg = (ActiveMQObjectMessage) message;
                serviceRequest = (ServiceRequest) msg.getObject();
                ServiceRequestType serviceRequestType = serviceRequest.getRequestType();
                //Process the json
                serviceRequestType.process(serviceRequest);
            } catch (Exception e) {
                LOG.error("Error processing json: " + (serviceRequest==null?"":serviceRequest.getJson()), e);

                //TODO respond back to client using the request id or set the status of the request id to failure

                //TODO Handle poison messages
            }
        }
    }

}
