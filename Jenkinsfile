pipeline {
    agent any

    tools {
        jdk 'jdk-11'
        maven 'maven-3.9.12'
    }

    parameters {
        string(name: 'HRR_MAJOR_VERSION', defaultValue: '2.2.0', description: 'The major version for naikeri-home-rerouting')
    }

    stages {
        stage('Set Version') {
            steps {
                echo "Setting version to ${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}"
                sh "mvn versions:set -DnewVersion=${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER} -DgenerateBackupPoms=false"
            }
        }

        stage('Build') {
            steps {
                script {
                    currentBuild.displayName = "#${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}"
                    currentBuild.description = "naikeri-home-rerouting"
                }
                sh "mvn clean install"
            }
        }

        // HRR is an application, not a library: nothing depends on it, so it is not deployed to
        // Artifactory. The jar needs its dependencies beside it and its configuration files in the
        // directory named by -DmainConfig.path, so all three are archived with the build.
        stage('Save Artifacts') {
            steps {
                archiveArtifacts artifacts: "home-re-routing/target/home-re-routing-${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}.jar, home-re-routing/target/lib/*.jar, home-re-routing/src/main/resources/*.xml",
                                 followSymlinks: false, onlyIfSuccessful: true
            }
        }
    }

    post {
        success { echo "Successfully built naikeri-home-rerouting ${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}" }
        failure { echo "Building naikeri-home-rerouting failed." }
    }
}
