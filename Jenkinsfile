pipeline {
    agent any

    environment{
        GIT_REPO_URL='git@10.12.170.176:arch/aiguibin-online-table.git'
        GIT_CREDENTIALS_ID='9284bf55-1e8f-4062-8d7d-af55f875280a'
        GIT_VERSION='main'

        DEPLOY_ENVIRONMENT="dev"
        PROJECT="aiguibin-online-table"
        BUILD_PROJECT_NAME="aiguibin-online-table"
        BUILD_APP_JAR_NAME="aiguibin-online-table.jar"

        HOME='/home/sys815'
        JENKINS_HOME="${HOME}/.jenkins/workspace"
        BUILD_PROJECT_SOURCE_DIRECTORY="${JENKINS_HOME}/${PROJECT}/target"
        DEPLOY_APP_DIRECTORY="${HOME}/deployApp/${PROJECT}"

        REMOTE_SERVER_IP="10.12.170.200"
        REMOTE_SERVER_USERNAME="yw"
        REMOTE_SERVER_DIR="/home/yw/${PROJECT}"
        START_SCRIPT_PATH="${JENKINS_HOME}/${PROJECT}/restart.sh"
        NODE_MODULES_DIR="/home/sys815/deployApp/webApp/nodeModules/online_table/node_modules.zip"
        VUE_CLI_SERVICE="${JENKINS_HOME}/${PROJECT}/src/main/webapp/node_modules/.bin/vue-cli-service"
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('Checkout Deploy App SCM'){
            steps{
                git branch:"${env.GIT_VERSION}",credentialsId: "${env.GIT_CREDENTIALS_ID}",url: "${GIT_REPO_URL}"
            }
        }
        stage('Maven Build APP'){
            steps{
                script{
                    def buildPath = "${env.JENKINS_HOME}/${env.PROJECT}"
                    println "Build Path : ${buildPath}"

                    def nodeModulesDir = "${buildPath}/src/main/webapp/node_modules"
                    if(fileExists(nodeModulesDir)){
                        echo "node_modules directory exists."
                    }else{
                        echo "node_modules directory does not exists."
                        sh "unzip ${NODE_MODULES_DIR} -d ${buildPath}/src/main/webapp/ "
                    }
                    sh "ln -sf /usr/local/nodejs/bin/node ${buildPath}/src/main/webapp/node/node"
                    sh "chmod +x ${VUE_CLI_SERVICE}"

                    dir(buildPath){
                        sh "rm -rf ${buildPath}/src/main/resources/static/"
                        sh "cd ${buildPath}/src/main/webapp/ && npm run build"
                        sh "cd ${buildPath} "
                        sh "mvn -Dfile.encoding=UTF-8 -Dmaven.skip.test=true -DskipTests=true clean install"
                    }
                }
           }
        }
        stage('Copy App To Deploy Directory') {
            steps {
                script{
                    def targetDir = "${env.DEPLOY_APP_DIRECTORY}/"
                    sh "mkdir -p ${targetDir}"
                    echo "Target Jar Dir: ${targetDir}"

                    sh "find ${env.BUILD_PROJECT_SOURCE_DIRECTORY} -type f -and -name ${BUILD_APP_JAR_NAME} | xargs -I {} mv {} ${targetDir}"
                }
            }
        }
        stage('Scp File Remote Server'){
            steps{
                script{
                    def remoteServer= { serverIp ->
                        def sshStr="${env.REMOTE_SERVER_USERNAME}@${serverIp}"

                        sh "scp ${env.DEPLOY_APP_DIRECTORY}/${env.BUILD_APP_JAR_NAME} ${sshStr}:${env.REMOTE_SERVER_DIR}"
                        sh "scp ${env.START_SCRIPT_PATH} ${sshStr}:${env.REMOTE_SERVER_DIR}"
                    }

        			remoteServer("${env.REMOTE_SERVER_IP}")
        		}
            }
        }
        stage('Remote Exec CMD  server'){
            steps{
                script{
                    def executeCmd = { serverIp ->
                        def sshStr="${env.REMOTE_SERVER_USERNAME}@${serverIp}"

                        sh "ssh ${sshStr} sh ${env.REMOTE_SERVER_DIR}/restart.sh"
                    }

                    executeCmd("${env.REMOTE_SERVER_IP}")
                }
            }
        }
    }

    post{
        always{
            echo '-------构建结束------------'
        }
        success{
            echo ' Deploy Project success...'
        }
        failure{
            echo ' Deploy Project failed....'
        }
    }
}
