package com.fig.app

import com.fig.domain.TaskDependency
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

import static com.fig.domain.TaskBuilder.task
import static com.fig.util.BindingUtil.toJson
import static groovyx.net.http.ContentType.URLENC

fig = new RESTClient( 'http://localhost:8080/Fig-0.0.1/fig/' )

deleteTasks()

def tasksToCreate = [task("a1").build(), task("b1").build(), task("c1").dependsOn(["a1", "b1"]).build()];
createTasks(toJson(tasksToCreate))
queryTasks('a1,b1,c1')

def tasksToUpdate = [task("a1").properties(["key1":"value1", "creationTime":new Date()]).build()];
updateTasks(toJson(tasksToUpdate))
queryTasks('a1,b1,c1')

def relationsToCreate = [new TaskDependency("b1", ["a1"])]
createTaskDependencies(toJson(relationsToCreate))
queryTasks('a1,b1,c1')

//def relationsToDelete = [new TaskDependency("c1", ["a1"])]
//deleteTaskDependencies(toJson(relationsToDelete)) // HTTPBuilder delete method does not accept a body
//queryTasks('a1,b1,c1')

// ~~~~~~~~~~~~~~~~~~~~ Create Task(s) ~~~~~~~~~~~~~~~~~~~~
def createTasks(String json){
    HttpResponseDecorator resp = fig.post( path : "task/create", body : "request=" + json, requestContentType : URLENC )
    println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
    if ( resp.isSuccess() ) {
        println resp.data
    } else {
        println "Error in create request: " + resp.data
    }
}

// ~~~~~~~~~~~~~~~~~~~~ Query Task ~~~~~~~~~~~~~~~~~~~~
def queryTasks(def taskNames){
    HttpResponseDecorator resp = fig.get(path : "task/query/${taskNames}" )
    println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
    if ( resp.isSuccess() ) {
        StringReader stringReader = resp.data
        stringReader.readLines().each {
            println it
        }
    }
}

// ~~~~~~~~~~~~~~~~~~~~ Update Task(s) ~~~~~~~~~~~~~~~~~~~~
def updateTasks(String json){
    HttpResponseDecorator resp = fig.put( path : "task/update", body : "request=" + json, requestContentType : URLENC )
    println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
    if ( resp.isSuccess() ) {
        println resp.data
    } else {
        println "Error in update request: " + resp.data
    }
}

// ~~~~~~~~~~~~~~~~~~~~ Delete All Tasks ~~~~~~~~~~~~~~~~~~~~
def deleteTasks(){
    HttpResponseDecorator resp = fig.delete( path : "task/deleteall", requestContentType : URLENC )
    if ( resp.isSuccess() ) {
        println "All tasks deleted successfully"
    }
}

// ~~~~~~~~~~~~~~~~~~~~ Create Task Dependencies ~~~~~~~~~~~~~~~~~~~~
def createTaskDependencies(String json){
    HttpResponseDecorator resp = fig.post(path : "relation/create", body : "request=" + json, requestContentType : URLENC)
    println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
    if ( resp.isSuccess() ) {
        println resp.data
    } else {
        println "Error in create request: " + resp.data
    }
}

// ~~~~~~~~~~~~~~~~~~~~ Delete Task Dependencies ~~~~~~~~~~~~~~~~~~~~
def deleteTaskDependencies(String json){
    HttpResponseDecorator resp = fig.delete(path : "relation/delete", body : "request=" + json, requestContentType : URLENC)
    println 'Status: ' + resp.status + ' - Content Type: ' + resp.contentType
    if ( resp.isSuccess() ) {
        println resp.data
    } else {
        println "Error in delete request: " + resp.data
    }
}