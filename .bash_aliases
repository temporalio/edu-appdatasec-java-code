alias workspace="cd ${GITPOD_REPO_ROOT}"
alias webui="gp preview $(gp url 8080)"

alias microservce="cd ${GITPOD_REPO_ROOT}/utilities/microservice && mvn exec:java -Dexec.mainClass="translationapi.Microservice""

alias ex1="cd ${GITPOD_REPO_ROOT}/exercises/custom-converter/practice"
alias ex1s="cd ${GITPOD_REPO_ROOT}/exercises/custom-converter/solution"
alias ex1w="mvn exec:java -Dexec.mainClass='customconverter.ConverterWorker'"
ex1st() {
    mvn exec:java -Dexec.mainClass="customconverter.Starter""
}



export PATH="$PATH:/workspace/bin"
echo "temporal configured! try typing temporal -v"
echo "Your workspace is located at: ${GITPOD_REPO_ROOT}"
echo "Type the command     workspace      to return to the workspace directory at any time."