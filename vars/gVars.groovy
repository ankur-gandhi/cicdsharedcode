@groovy.transform.Field
def Opt = [:]
@groovy.transform.Field
def PSN=''

def prn(param){
    def sepTxt = '*************************************************************************************************\n'
    print "${sepTxt}${param}\n${sepTxt}"
}
def checkForEmpty(param){
    if(param != null && param != ''){
        return true
    }
    return false
}