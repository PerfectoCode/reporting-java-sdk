library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave-java17' }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    }
    stages {
        
        stage('CLEAN UP') {
            steps {
                cleanWs()
            }
        }   
        stage('SETUP BUILD') {
            steps {
                script {
                    reportiumPipeline.setupBuild()
                }
            }
        }
        stage('BUILD') { 
            steps {
                script {
                    buildCode()
                    echo "artifactTag - is ${artifactTag}"
                }
            }
        }
        // stage('Test') {
        //     steps {
        //         script {
        //          reportiumSdkVersion = "${artifactTag}"
        //          jobBuild = build job: "reportium-sdk-java-test/master", parameters: [
        //          string(name: "reportiumSdkVersion", value: "${reportiumSdkVersion}")],propagate: true, wait: true
                
        //         }
        //     }
        // }
        // stage('WHITESOURCE SCAN') {
        //     steps {
        //         script {
        //             reportiumPipeline.trigger_whitesource_scan()
        //         }
        //     }
        // }
    }
     post {
        always {
            script {
                reportiumPipeline.buildStatusNotification()
            }
        }
    }   
}
// ...existing code above unchanged...

def buildCode() {
    dir("$WORKSPACE/source") {
        try {
            withMaven(
                maven: 'Maven latest',
                mavenOpts: '-Xmx2048m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/temp/dump.hprof',
                mavenLocalRepo: '/home/ubuntu/.m2/repository') {

                sh(script: "mvn build-helper:parse-version versions:set -DnewVersion=${artifactTag}");
                sh(script: 'mvn clean install -Pcode-coverage-validation');

                // Determine PR vs master
                boolean isPrBuild = (env.CHANGE_ID?.trim()) || (BRANCH_NAME != 'master')
                echo "isPrBuild = ${isPrBuild}"
                

                // Conditional deploy target
                def deployCmd = 'mvn deploy -DskipTests -Ppackage-stuff'
                if (isPrBuild) {
                    def prRepo = 'snapshots::default::http://reporting-new-nexus.aws-dev.perfectomobile.com/repository/maven-snapshots'
                    deployCmd += " -DaltDeploymentRepository=${prRepo}"
                    echo "PR/non-master build -> deploying to new Nexus: ${prRepo}"
                } else {
                    echo "Master build -> using distributionManagement from pom.xml"
                }

                sh(script: deployCmd)

            }

            sh(script: "git tag ${artifactTag} ${commitHash}");
            withCredentials([usernamePassword(credentialsId: '2cb048f3-6369-4220-bbb7-2668527a8c22', passwordVariable: 'GIT_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                sh "git push https://${GIT_USERNAME}:${GIT_TOKEN}@${repositoryUrl} --tags"
            }
        } catch (error) {
            echo "Current build currentResult (buildCode - catch): ${currentBuild.currentResult}"
            reportiumSlack.sendSlackMessage("${slackChannel}", "failed to compile the code, build aborted", "#e02814");
            throw error;
        }
    }
    echo "Current build currentResult (buildCode): ${currentBuild.currentResult}"
}
// ...existing code after unchanged...