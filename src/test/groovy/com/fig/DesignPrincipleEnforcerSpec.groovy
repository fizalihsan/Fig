package com.fig

import com.thoughtworks.qdox.JavaProjectBuilder
import groovy.io.FileType
import org.apache.commons.io.FilenameUtils
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 12:33 PM
 */
class DesignPrincipleEnforcerSpec extends Specification {

    //TODO Java source path is hardcoded since there is no way to scan through the source files without providing
    //absolute path. Reflection can't be used since the 'import' details are lost in bytecode. Source files could
    //be included in the jar file but jarring happens only after the unit test completes successfully.
    @Shared def javaSourcePath = new File('C:\\Fizal\\WorkArea\\SourceCode\\GitHubHome\\Fig\\src\\main\\java')

    @Shared def packageVsClasses = [
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

    @Ignore
    def "Leakage of Concern Check - Plain vanilla Java solution"() {
        //TODO Java source path is hardcoded since there is no way to scan through the source files without providing
        //absolute path. Reflection can't be used since the 'import' details are lost in bytecode. Source files could
        //be included in the jar file but jarring happens only after the unit test completes successfully.
        def leakages = []

        javaSourcePath.eachFileRecurse(FileType.FILES) { file ->
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

    @Ignore
    def "Leakage of Concern Check - Using QDox library"() {
        def leakages = []

        // Get the ClassLibrary
        JavaProjectBuilder builder = new JavaProjectBuilder();
        // Add a sourcefolder;
        builder.addSourceTree( javaSourcePath );

        builder.getSources().each { source ->
            def imports = source.imports
            def classes = source.getClasses()

            packageVsClasses.each { packageName, allowedClasses ->
                imports.each { importStmt ->
                    if(importStmt.contains(packageName)){
                        def fileName = classes[0].getName() + ".java"
                        if(!allowedClasses.contains(fileName)){
                            leakages << "$fileName:$importStmt - $fileName"
                        }
                    }
                }
            }
        };

        println "Leakage of Concerns:"
        leakages.each { println it }

        when: true == true
        then: leakages.size() == 0
    }
}