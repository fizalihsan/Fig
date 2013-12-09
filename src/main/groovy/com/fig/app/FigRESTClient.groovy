package com.fig.app
import com.fig.domain.Task
import com.fig.domain.TaskDependency
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

import static com.fig.util.BindingUtil.toJson
import static groovyx.net.http.ContentType.URLENC

class FigRESTClient {

    def restClient;
    FigRESTClient(def defaultUri = 'http://localhost:8080/Fig-0.0.1/fig/') {
        restClient = new RESTClient(defaultUri)
    }

    /**
     * Create tasks
     * @param json
     * @return
     */
    def createTasksFromJson(String json) {
        HttpResponseDecorator resp = restClient.post(path: "task", body: "request=" + json, requestContentType: URLENC)
        println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
        if (resp.isSuccess()) {
            println resp.data
        } else {
            println "Error in create request: " + resp.data
        }
    }

    /**
     * Create tasks from given collection
     * @param tasks
     * @return
     */
    def createTasks(Collection<Task> tasks){
        createTasksFromJson(toJson(tasks))
    }

    /**
     * Query Tasks
     * @param taskNames
     * @return
     */
    def queryTasks(def taskNames) {
        HttpResponseDecorator resp = restClient.get(path: "task/${taskNames}")
        println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
        if (resp.isSuccess()) {
            StringReader stringReader = resp.data
            stringReader.readLines().each {
                println it
            }
        }
    }

    /**
     * Update Tasks
     * @param json
     * @return
     */
    def updateTasksInJson(String json) {
        HttpResponseDecorator resp = restClient.put(path: "task", body: "request=" + json, requestContentType: URLENC)
        println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
        if (resp.isSuccess()) {
            println resp.data
        } else {
            println "Error in update request: " + resp.data
        }
    }


    /**
     * Update tasks from given collection
     * @param tasks
     * @return
     */
    def updateTasks(Collection<Task> tasks){
        updateTasksInJson(toJson(tasks))
    }

    /**
     * Delete all tasks
     */
    def deleteAllTasks() {
        HttpResponseDecorator resp = restClient.delete(path: "task/deleteall", requestContentType: URLENC)
        if (resp.isSuccess()) {
            println "All tasks deleted successfully"
        }
    }

    /**
     * Create task dependencies
     * @param json
     * @return
     */
    def createTaskDependenciesFromJson(String json) {
        HttpResponseDecorator resp = restClient.post(path: "relation/create", body: "request=" + json, requestContentType: URLENC)
        println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
        if (resp.isSuccess()) {
            println resp.data
        } else {
            println "Error in create request: " + resp.data
        }
    }

    /**
     * Create task dependencies from given collection
     * @param tasks
     * @return
     */
    def createTaskDependencies(Collection<TaskDependency> dependencies){
        createTaskDependenciesFromJson(toJson(dependencies))
    }

    /**
     * Delete task dependencies
     * @param json
     * @return
     */
    def deleteTaskDependenciesFromJson(String json) {
        HttpResponseDecorator resp = restClient.delete(path: "relation/delete", body: "request=" + json, requestContentType: URLENC)
        println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
        if (resp.isSuccess()) {
            println resp.data
        } else {
            println "Error in delete request: " + resp.data
        }
    }

    /**
     * Delete task dependencies from given collection
     * @param tasks
     * @return
     */
    def deleteTaskDependencies(Collection<TaskDependency> dependencies){
        deleteTaskDependenciesFromJson(toJson(dependencies))
    }

}