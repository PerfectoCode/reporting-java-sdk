library 'aws-access-keys@master'

pipeline {
    agent { label 'ubuntu-build-slave-java17' }
    parameters {
        string(name: 'SDK_RELEASE_TAG', defaultValue: '', description: 'Supply version to deploy to repo1 (e.g., 4.4.100). Leave empty for snapshot deployment.')
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
                    // Echo final artifactTag for traceability (instruction #5)
                    echo "Build: branch=${BRANCH_NAME}, artifactTag=${artifactTag}, SDK_RELEASE_TAG=${params.SDK_RELEASE_TAG}"
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
            steps {
                script {
                    def testSdkVersion = env.CHANGE_ID ? artifactTag : (params.SDK_RELEASE_TAG ?: artifactTag)
                    echo "Triggering SDK tests with version: ${testSdkVersion}"
                    build job: "reportium-sdk-java-test/master", 
                          parameters: [string(name: "reportiumSdkVersion", value: "${testSdkVersion}")],
                          propagate: true, 
                          wait: true
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

                // Deployment logic per instruction #5: echo for traceability
                boolean isReleaseBuild = (BRANCH_NAME == 'master') && (params.SDK_RELEASE_TAG?.trim())
                boolean isPrBuild = (env.CHANGE_ID?.trim())
                boolean isMasterSnapshot = (BRANCH_NAME == 'master') && !params.SDK_RELEASE_TAG?.trim()
                
                echo "Deployment decision: branch=${BRANCH_NAME}, isRelease=${isReleaseBuild}, isPR=${isPrBuild}, isMasterSnapshot=${isMasterSnapshot}"

                if (isPrBuild) {
                    // PR: deploy snapshot to reporting-new-nexus
                    def snapshotRepo = 'snapshots::default::http://reporting-new-nexus.aws-dev.perfectomobile.com/repository/maven-snapshots'
                    sh(script: "mvn deploy -DskipTests -Ppackage-stuff -DaltDeploymentRepository=${snapshotRepo}")
                    echo "PR build -> deployed SNAPSHOT ${artifactTag} to ${snapshotRepo}"
                    
                } else if (isMasterSnapshot) {
                    // Master without SDK_RELEASE_TAG: deploy snapshot to reporting-new-nexus
                    def snapshotRepo = 'snapshots::default::http://reporting-new-nexus.aws-dev.perfectomobile.com/repository/maven-snapshots'
                    sh(script: "mvn deploy -DskipTests -Ppackage-stuff -DaltDeploymentRepository=${snapshotRepo}")
                    echo "Master snapshot build -> deployed ${artifactTag} to ${snapshotRepo}"
                    
                } else if (isReleaseBuild) {
                    // Master with SDK_RELEASE_TAG: deploy release to repo1 via distributionManagement
                    sh(script: 'mvn deploy -DskipTests -Ppackage-stuff')
                    echo "Master release build -> deployed version ${artifactTag} to repo1 via pom.xml distributionManagement"
                    
                    // Git tag release (instruction #5: artifact flow)
                    sh(script: "git tag ${artifactTag} ${commitHash}")
                    withCredentials([usernamePassword(credentialsId: '2cb048f3-6369-4220-bbb7-2668527a8c22', passwordVariable: 'GIT_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                        sh "git push https://${GIT_USERNAME}:${GIT_TOKEN}@${repositoryUrl} --tags"
                    }
                } else {
                    echo "No deployment: branch=${BRANCH_NAME}, no release tag"
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