o pipeline {
    agent any
    stages{
        stage('build gradle service'){
            steps{
              //  checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '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', url: 'https://github.com/Ashish-200/bank-master.git']])
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Ashish-200/bank-master2']])
              //sh 'sudo snap install gradle --classic'
                // .
                sh 'echo "Hello"'
                sh 'echo gradle --version'
                sh 'chmod +x gradlew'
                sh 'gradle wrapper'
                sh './gradlew clean build -g gradle-user-home'
            }
        }
       
    }
}
