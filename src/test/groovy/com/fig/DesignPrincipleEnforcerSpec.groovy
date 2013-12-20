package com.fig

import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 12:33 PM
 */
class DesignPrincipleEnforcerSpec extends Specification {

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

    /**
     * This is a unique test case to scan through the source Java files to look for design principle breaches.
     * For example, in order to confine certain 3rd party library usage within a limited set of classes, one can define the library pkg names and class names allowed.
     * If a class not registered here references one of those libraries, this unit test would fail.
     */
    def "Leakage of Concern Check - Reading from GitHub zip file"(){
        //Step 1: Get all the Java files from remote GitHub repository
        def githubZip = "https://github.com/fizalihsan/Fig/archive/master.zip"

        ZipInputStream zipInputStream = null
        def javaFiles = [:]
        try {
            zipInputStream = new ZipInputStream(new URL(githubZip).openStream());
            ZipEntry zipEntry;

            while( (zipEntry = zipInputStream.getNextEntry())!=null ) {
                if(!zipEntry.isDirectory() && zipEntry.getName().endsWith("java")){
                    StringWriter stringWriter = new StringWriter()
                    IOUtils.copy(zipInputStream, stringWriter)
                    def fileContent = stringWriter.toString();

                    javaFiles[zipEntry.getName()] = fileContent
                    stringWriter.close()
                }
            }
        } catch (Exception e){
            e.printStackTrace()
            assert !true
        } finally {
            zipInputStream.close()
        }

        //Step 2: Parse through the Java file contents and check for leakages
        def leakages = []

        javaFiles.each { fileName, content ->
            def lines = content.split("\\\n");
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines[lineIndex]

                packageVsClasses.each { packageName, allowedClasses ->
                    if(line.contains(packageName)){
                        def baseName = FilenameUtils.getName(fileName);
                        if(!allowedClasses.contains(baseName)){
                            leakages << "$fileName:$lineIndex - $line - $baseName"
                        }
                    }
                }

            }
        }

        println "Leakage of concerns found:"
        leakages.each { println it }

        when: true == true
        then: leakages.size() == 0
    }

    @Ignore
    def "Leakage of Concern Check - Reading from GitHub via GitHub API"(){
        def githubZip = "https://github.com/fizalihsan/Fig/archive/master.zip"

        GitHub gitHub = GitHub.connectAnonymously()
        GHRepository repository = gitHub.getRepository("fizalihsan/Fig")
        GHBranch masterBranch = repository.getBranches().get(repository.getMasterBranch());
        println("")
        when : true
        then : true
    }
}