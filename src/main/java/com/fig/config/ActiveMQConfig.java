package com.fig.config;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 12/10/13
 * Time: 9:50 AM
 */
public class ActiveMQConfig {
    private String brokerName;
    private String brokerURI;
    private String dataFolderLocation;
    private String requestQueue;

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerURI() {
        return brokerURI;
    }

    public void setBrokerURI(String brokerURI) {
        this.brokerURI= brokerURI;
    }

    public String getDataFolderLocation() {
        return dataFolderLocation;
    }

    public void setDataFolderLocation(String dataFolderLocation) {
        this.dataFolderLocation = dataFolderLocation;
    }

    public String getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(String requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ActiveMQConfig{");
        sb.append("brokerName='").append(brokerName).append('\'');
        sb.append(", brokerURI='").append(brokerURI).append('\'');
        sb.append(", dataFolderLocation='").append(dataFolderLocation).append('\'');
        sb.append(", requestQueue='").append(requestQueue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
