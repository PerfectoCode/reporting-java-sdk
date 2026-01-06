library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave-java17' }
    parameters {
        string(name: 'SDK_RELEASE_TAG', defaultValue: '', description: 'Supply version to deploy to Nexus (e.g., 4.4.100). Leave empty to skip deployment.')
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
                    if (params.SDK_RELEASE_TAG?.trim()) {
                        env.artifactTag = params.SDK_RELEASE_TAG
                        echo "Using supplied release version: ${artifactTag}"
                    } else {
                        echo "${BRANCH_NAME} build without SDK_RELEASE_TAG -> build-only mode (no deployment/test)"
                    }
                }
            }
        }
        stage('BUILD') { 
            steps {
                script {
                    buildCode()
                }
            }
        }
        stage('Test') {
            when {
                expression { 
                    ((BRANCH_NAME == 'test-pr')) && params.SDK_RELEASE_TAG?.trim()
                }
            }
            steps {
                script {
                    reportiumSdkVersion = "${artifactTag}"
                    jobBuild = build job: "reportium-sdk-java-test/master", parameters: [
                        string(name: "reportiumSdkVersion", value: "${reportiumSdkVersion}")
                    ], propagate: true, wait: true
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

                sh(script: "mvn build-helper:parse-version versions:set -DnewVersion=${artifactTag}")
                sh(script: 'mvn clean install -Pcode-coverage-validation')

                // Deployment logic: test-pr mimics master for testing, PRs deploy snapshots
                boolean isMasterRelease = ((BRANCH_NAME == 'master') || (BRANCH_NAME == 'test-pr')) && (params.SDK_RELEASE_TAG?.trim())
                boolean isPrBuild = (env.CHANGE_ID?.trim())
                
                echo "Deployment decision: branch=${BRANCH_NAME}, isMasterRelease=${isMasterRelease}, isPrBuild=${isPrBuild}, artifactTag=${artifactTag}"

                if (isPrBuild || isMasterRelease) {
                    def deployCmd = 'mvn deploy -DskipTests -Ppackage-stuff'
                   
                    if (isPrBuild) {
                        def snapshotRepo = 'snapshots::default::http://reporting-new-nexus.aws-dev.perfectomobile.com/repository/maven-snapshots'
                        deployCmd += " -DaltDeploymentRepository=${snapshotRepo}"
                        echo "PR build -> deploying SNAPSHOT to ${snapshotRepo}"
                    } else {
                        echo "Release build (${BRANCH_NAME}) -> deploying version ${artifactTag} via pom.xml distributionManagement"
                    }
                    
                    sh(script: deployCmd)
                } else {
                    echo "${BRANCH_NAME} build without SDK_RELEASE_TAG -> skipping deployment"
                }
            }

            // Git tag for master or test-pr releases (remove test-pr before merge)
            if (((BRANCH_NAME == 'master') || (BRANCH_NAME == 'test-pr')) && (params.SDK_RELEASE_TAG?.trim())) {
                sh(script: "git tag ${artifactTag} ${commitHash}")
                withCredentials([usernamePassword(credentialsId: '2cb048f3-6369-4220-bbb7-2668527a8c22', passwordVariable: 'GIT_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                    sh "git push https://${GIT_USERNAME}:${GIT_TOKEN}@${repositoryUrl} --tags"
                }
            }
        } catch (error) {
            echo "Current build currentResult (buildCode - catch): ${currentBuild.currentResult}"
            reportiumSlack.sendSlackMessage("${slackChannel}", "failed to compile the code, build aborted", "#e02814")
            throw error
        }
    }
    echo "Current build currentResult (buildCode): ${currentBuild.currentResult}"
}