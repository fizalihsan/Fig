package com.fig.domain
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 12:40 PM
 */
class TaskBuilderSpec extends Specification{
    def "Task with no name"() {
        when: task(null).build();
        then: thrown(RuntimeException)
    }

    def "Task with no dependencies"(){
        when: def task = task("taskName").build();
        then:
        task != null
        task.name == "taskName"
        task.dependsOn == null
        task.properties == null
    }

    def "Task with no properties"(){
        when: def task = task("taskName").dependsOn(["Task1", "Task2"] as Set).build();
        then:
        task != null
        task.name == "taskName"
        task.dependsOn.size() == 2
        task.dependsOn.contains("Task1")
        task.dependsOn.contains("Task2")
        task.properties == null
    }

    def "Task with dependencies and properties"(){
        when:
        def task = task("taskName")
                .dependsOn(["Task1", "Task2"] as Set)
                .properties(["key1":"value1", "key2":"value2"])
                .build();
        then:
        task != null
        task.name == "taskName"
        task.dependsOn.size() == 2
        task.dependsOn.contains("Task1")
        task.dependsOn.contains("Task2")
        task.properties.size() == 2
    }
}
