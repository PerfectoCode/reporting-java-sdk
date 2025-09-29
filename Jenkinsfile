library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave-java17' }
    parameters {
        string(name: 'SDK_RELEASE_TAG', defaultValue: '', description: 'SDK Release Tag', trim: true)
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
                    echo "artifactTag - is ${artifactTag}"
                    if (params.SDK_RELEASE_TAG?.trim()) {
                        env.artifactTag = params.SDK_RELEASE_TAG.trim()
                        echo "artifactTag overridden by SDK_RELEASE_TAG -> ${env.artifactTag}"
                    }
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
                    def reportiumSdkVersion = "${artifactTag}"

                    jobBuild = build job: "reportium-sdk-java-test/master", parameters: [
                        string(name: "reportiumSdkVersion", value: "${reportiumSdkVersion}")
                    ], propagate: true, wait: true

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
