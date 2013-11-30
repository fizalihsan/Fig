package com.fig.manager

import com.fig.manager.TaskManager
import groovy.transform.Field

import static com.fig.domain.TaskBuilder.task

createTasks();

//-------------------------------------------------------------
@Field def mgr = new TaskManager();

def newtask(String name, def ... dependsOn) {
    return task(name).dependsOn(dependsOn as Set).build();
};

def createTasks() {
    def a1 = newtask("a1", "b1");
    def b1 = newtask("b1");
    def a2 = newtask("a2", "b2");
    def b2 = newtask("b2", "c2");
    def c2 = newtask("c2");

    mgr.createTasks([
            a1, b1,
            a2, b2, c2
    ])
}

def printAllTasks(){
    mgr.get
}

