pipeline {
    agent any
    stages{
        stage('build gradle service'){
            steps{
              //  checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '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', url: 'https://github.com/Ashish-200/bank-master.git']])
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Ashish-200/bank-master2']])
              //sh 'sudo snap install gradle --classic'
                // .
                sh 'wget https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip -P /tmp'
                sh 'sudo unzip -d /opt/gradle /tmp/gradle-${VERSION}-bin.zip'
              //sh 'gradle --version'
                sh 'java -version'
                sh 'pwd'
                sh 'ls'
                sh 'gradle --version'
               // sh 'chmod +x gradlew'
                //sh 'gradle wrapper'
               // sh './gradlew clean build'
                //sh 'gradle build'
            }
        }
       
    }
}
