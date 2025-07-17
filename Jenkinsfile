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
                    reportiumPipeline.buildCode()
                    echo "artifactTag - is ${artifactTag}"
                }
            }
        }
        stage('Test') {
            steps {
                script {
                 reportiumSdkVersion = "${artifactTag}"
                 jobBuild = build job: "reportium-sdk-java-test/master", parameters: [
                 string(name: "reportiumSdkVersion", value: "${reportiumSdkVersion}")],propagate: false, wait: true
                
                }
            }
        }
        stage('WHITESOURCE SCAN') {
            steps {
                script {
                    reportiumPipeline.trigger_whitesource_scan()
                }
            }
        }
    }   
}
