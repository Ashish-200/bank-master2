pipeline {
    agent any
    stages{
        stage('build gradle service'){
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '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', url: 'https://github.com/Ashish-200/bank-master.git']])
                sh './gradlew clean build'
            }
        }
       
    }
}
