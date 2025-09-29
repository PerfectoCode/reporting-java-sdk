library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave-java17' }
    parameters {
        string(name: 'SDK_RELEASE_TAG', defaultValue: '', description: 'Optional explicit SDK version to test')
    }
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
                // Pick provided SDK_RELEASE_TAG if non-empty, else fallback to artifactTag
                 def reportiumSdkVersion = params.SDK_RELEASE_TAG?.trim() ? params.SDK_RELEASE_TAG.trim() : artifactTag
                 echo "Using reportiumSdkVersion=${reportiumSdkVersion}"

                 jobBuild = build job: "reportium-sdk-java-test/master", parameters: [
                 string(name: "reportiumSdkVersion", value: "${reportiumSdkVersion}")],propagate: true, wait: true
                
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
     post {
        always {
            script {
                reportiumPipeline.buildStatusNotification()
            }
        }
    }   
}
