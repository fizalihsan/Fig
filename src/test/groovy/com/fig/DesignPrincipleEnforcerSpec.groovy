package com.fig

import groovy.io.FileType
import org.apache.commons.io.FilenameUtils
import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 12:33 PM
 */
class DesignPrincipleEnforcerSpec extends Specification {

    def "Leakage of Concern Check"() {
        def packageVsClasses = [
                "org.apache.activemq" : [
                        "MessagingUtil.java",
                        "ServiceRequestConsumer.java"
                ],
                "com.google.gson" : [
                        "BindingUtil.java",
                        "SuccessResponse.java"
                ],
                "org.neo4j." : [
                        "Neo4jHelper.java",
                        "Neo4jTaskAdapter.java",
                        "TaskRelations.java",
                        "PathPrinter.java",
                        "TransactionWrapper.java"
                ],
                "javax.ws.rs." : [
                        "MainApplication.java",
                        "TaskResource.java",
                        "StatusResource.java",
                        "ValidationResponse.java" //TODO avoid having JAX references in this class
                ]
        ]

        //TODO Java source path is hardcoded since there is no way to scan through the source files without providing
        //absolute path. Reflection can't be used since the 'import' details are lost in bytecode. Source files could
        //be included in the jar file but jarring happens only after the unit test completes successfully.
        def srcDir = new File('C:\\Fizal\\WorkArea\\SourceCode\\GitHubHome\\Fig\\src\\main\\java')
        def leakages = []

        srcDir.eachFileRecurse(FileType.FILES) { file ->
            if (file.name =~ /.*\.java/) {
                file.readLines().eachWithIndex { String line, int lineIndex ->

                    packageVsClasses.each { packageName, allowedClasses ->
                        if(line.contains(packageName)){
                            def baseName = FilenameUtils.getName(file.name);
                            if(!allowedClasses.contains(baseName)){
                                leakages << "$file:$lineIndex - $line - $baseName"
                            }
                        }
                    }

                }
            }
        }

        println "Leakage of Concerns:"
        leakages.each { println it }

        when: true == true
        then: leakages.size() == 0
    }
}