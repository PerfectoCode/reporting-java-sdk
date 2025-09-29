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
                    setupBuild()
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


def buildCode() {
    dir("$WORKSPACE/source") {
        try {
            withMaven(
                    maven: 'Maven latest',
                    mavenOpts: '-Xmx2048m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/temp/dump.hprof',
                    mavenLocalRepo: '/home/ubuntu/.m2/repository') {
                sh(script: "mvn build-helper:parse-version versions:set -DnewVersion=${artifactTag}");
                sh(script: 'mvn clean install -Pcode-coverage-validation');
                switch (BRANCH_NAME) {
                    case 'master':
                    /*   Disbale Whitesource
                        sh('''
                            #!/bin/bash
                            aws s3 cp s3://unified-agent/wss-unified-agent-20.10.1.jar .
                            java -jar wss-unified-agent-20.10.1.jar -detect -c wss-generated-file.config
                            java -jar wss-unified-agent-20.10.1.jar -wss.url https://saas-eu.whitesourcesoftware.com/agent -apiKey e1ebdc4fe28549e5a71bd790288ca2c27f88543eac8e4a6e9f4bedc1763b3810 -product Perfecto -project ${project} -c wss-generated-file.config
                        ''') */
                        break;
                    default:
                        break;
                }
                sh(script: 'mvn deploy -DskipTests -Ppackage-stuff -X');
            }
            sh(script: "git tag ${artifactTag} ${commitHash}");
            withCredentials([usernamePassword(credentialsId: '2cb048f3-6369-4220-bbb7-2668527a8c22	', passwordVariable: 'GIT_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
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


def setupBuild() {
    dir("$WORKSPACE/source") {
        checkout scm;
        env.repositoryUrl = sh(script: 'git config --get remote.origin.url', returnStdout: true).trim().split('://')[1];
        env.project = sh(script: 'basename -s .git `git config --get remote.origin.url`', returnStdout: true).trim();
        env.commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim();
        env.PREFIX = sh(script: "echo $commitHash", returnStdout: true).take(9)
        env.commiterMail = sh(script: 'git --no-pager show -s --format=\'%ae\'', returnStdout: true).trim();
        env.committerName = sh(script: 'git log -1 --pretty=format:\'%an\'', returnStdout: true).trim();
        env.slackChannel = reportiumSlack.getSlackChannel(commiterMail);
        switch (BRANCH_NAME) {
            case 'master':
            if (params.SDK_RELEASE_TAG?.trim()) {
                env.artifactTag = params.SDK_RELEASE_TAG.trim()
            } else {                                                    
                env.artifactTag = "1.1.${BUILD_NUMBER}";
            }
                break;
            default:
                env.artifactTag = "1.1.${JOB_BASE_NAME}.${BUILD_NUMBER}-SNAPSHOT"; // JOB_BASE_NAME is usually "PR-XXX"
                reportiumSlack.sendSlackMessage("${slackChannel}", "Build ${JOB_NAME}: STARTED :star:", "#14e028");
                break;
        }
    }
    sh 'env'
    echo "Current build currentResult (setupBuild): ${currentBuild.currentResult}"
}