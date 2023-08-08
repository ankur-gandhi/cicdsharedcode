package sfdx.utils;

import java.io.*
import hudson.model.*
import javax.mail.*
import java.net.*
import groovy.io.*

class Common {
    def workspace
    def steps
    def Opt
    Common(){}
    def startUp(steps, opt){
        this.steps = steps
        this.workspace = steps.WORKSPACE
        this.Opt = opt
    }
    def cmd
    

    @NonCPS
    def generateApexUnitTestClassList(classFPath,includeInterfaceClasses,includeOrphanTestClasses){
        println "Start generateApexUnitTestClassList\n"
        def testClassList = [];
        def dir = new File(classFPath)
        // for each apex class in the deployment package that is not a test class, run it's sibling test class
        if(dir != null && dir.exists()){
            def fx = new File(classFPath).eachFileRecurse(FileType.FILES) { file ->

            //new File(classFPath).traverse(type: groovy.io.FileType.FILES) { file ->
            
                def fileContent = file.text
                def contentLowerCase=fileContent.toLowerCase()
                if(file.name.contains('-meta.xml')){
                    // do nothing. it's not a class
                }else if(contentLowerCase.contains('@istest')){
                    if(includeOrphanTestClasses=='true'){
                        // add the class
                        println "Adding OrphanTestClass ${file.name}\n"
                        testClassList.add(file.name.replace('.cls',''))
                    }else{
                        // do nothing. it's a test class
                    }
                }else if(!includeInterfaceClasses && contentLowerCase.contains('public interface')){
                    // do nothing. it's an interfce class

                }else{
                    // add the class
                    println "Adding Test Class of Class ${file.name}\n"
                    testClassList.add((file.name).replace('.cls','_Test'))
                }
            }
        }
        println "Apex Test Class List: $testClassList"
        //writeToFile(steps.WORKSPACE+"/deploy/specifiedTests.txt",list.join(","))
        return testClassList;
    }
    def writeToFile(fileLoc,value){
        ssh("rm -rf $fileLoc")
        def setFile = new File(fileLoc)
        setFile.write value
    }

    
    def mapFileVals(def fileList) {
        def fileMap = [:]
        fileList.each{file1 ->
            def fileVal = new File(file1).getText('UTF-8')
            fileMap.put(file1,fileVal)
        }
        return fileMap;
    }

    def prn(logType,sepType,txt) {
        def sepTxt = ''
        if(sepType=='H1'){
            sepTxt = '*************************************************************************************************'
        }else if(sepType=='H2'){
            sepTxt = '************************'
        }else if(sepType=='Paragraph'){
            sepTxt = ''
        }else if(sepType=='Line'){
            sepTxt = ''
        }
        
        def LogSetting =''
        try{LogSetting = steps.DebugLogs}catch(all){}

        def prLog = false;
        if(logType=='None'){
            prLog=true
        }else if(LogSetting=='Info' && logType=='Info'){
            prLog=true
        }else if(LogSetting=='Finest' && (logType=='Finest' || logType=='Info' )){
            prLog=true
        }
        if(prLog){
            if(sepType =='H1'){
                steps.print "\n$sepTxt\n* $txt \n$sepTxt\n"
            }else if(sepType =='H2'){
                steps.print "$sepTxt\n* $txt \n$sepTxt"
            }else if(sepType =='Paragraph'){
                steps.print "$txt"
            }else if(sepType =='Line'){
                steps.print "$txt"
            }else{
            }
        }
        
    }
    def ssh(cmd) {
	    steps.sh('#!/bin/sh -e\n' + cmd)
    }
    def sshRtn(cmd) {
        return steps.sh (
            script: '#!/bin/sh -e\n' + cmd,
            returnStdout: true
        ).trim()
    }
    def shRtn(cmd) {
        return steps.sh (
            script: cmd,
            returnStdout: true
        ).trim()
    }

    @NonCPS
    def eachFileRecurseHelper(folderLoc,searchString,incExcl) {
        def fileList = []
        try{
            def fx = new File("$folderLoc").eachFileRecurse(FileType.FILES) {
                if(searchString==null || searchString==''){
                    fileList << it.getAbsolutePath()
                }else{
                    if(incExcl){
                        if(it.name.contains(searchString)) {
                            fileList << it.getAbsolutePath()
                        }
                    }else{
                        if(!(it.name).contains(searchString)) {
                            fileList << it.getAbsolutePath()
                        }
                    }
                }
            }
        }catch(all){

        }
        return fileList
    }

    
}