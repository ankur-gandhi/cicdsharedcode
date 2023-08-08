package sfdx.utils;

class Init {

    def workspace
    def steps
    Init(){}
    def startUp(steps){
        this.steps = steps
        this.workspace = steps.WORKSPACE
    }
    def setOptions(Opt){
        try{Opt.put('workspace',steps.WORKSPACE)}catch(all){}

        Opt = updateOptions(Opt)
        return Opt
    }
    def updateOptions(Opt){
        // logic goes here for updating options  (without running set options)
        return Opt
    }
}