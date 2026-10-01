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

        stage('Release') {
            steps {
                withAnt(installation: 'Ant_1.10.15') {
                    dir('release') {
                        sh "ant -f build.xml -Dskip.maven.build=true -Dnaikeri.hrr.release.version=${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}"
                    }
                }
            }
        }

        // HRR is an application, not a library: nothing depends on it, so it is not deployed to
        // Artifactory. The release zip carries the jar, its dependencies, the configuration and the
        // admin guide, with bin/start.sh to run it.
        stage('Save Artifacts') {
            steps {
                archiveArtifacts artifacts: "release/Naikeri-HRR-${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}.zip", followSymlinks: false, onlyIfSuccessful: true
            }
        }
    }

    post {
        success { echo "Successfully built naikeri-home-rerouting ${params.HRR_MAJOR_VERSION}-${BUILD_NUMBER}" }
        failure { echo "Building naikeri-home-rerouting failed." }
        always  { sh 'rm -rf release/target release/Naikeri-HRR-*.zip' }
    }
}
