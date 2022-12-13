library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave' }
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
                    reportiumPipeline.buildCode()
                    echo "artifactTag - is ${artifactTag}"
                }
            }
        }

        stage('Test') {
            steps {
                script {

                 echo "artifactTag - is ${artifactTag}"   
                 jobBuild = build job: "reportium-sdk-java-test/boris", parameters: [
                 string(name: "artifactTag", value: "${artifactTag}")],propagate: false, wait: true
                
             }
         }
     }
 }
}
