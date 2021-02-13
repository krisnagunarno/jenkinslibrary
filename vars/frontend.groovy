#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent {
            label 'manager'
        }

        environment {
            registry = "kgunarno/bliblimart-frontend"
            registryCredential = "Krisna-24"
            dockerImage = ''
            scannerImage = "arminc/clair-local-scan"
            scannerDB = "arminc/clair-db" 
        }

        stages {
            stage('Build and Dockerized Vue Project') {
                steps {
                    script {
                        dockerImage = docker.build registry
                    }
                }
            }

//            stage('Analyze Docker Image') {
//                steps {
//                    sh '''
//                        docker run -p 5432:5432 -d --name db ${scannerDB}
//                        sleep 15
//                        docker run -p 6060:6060 --link db:postgres -d --name clair ${scannerImage}
//                        sleep 1
//                        DOCKER_GATEWAY=$(docker network inspect bridge --format "{{range .IPAM.Config}}{{.Gateway}}{{end}}")
//                        wget -qO clair-scanner https://github.com/arminc/clair-scanner/releases/download/v8/clair-scanner_linux_amd64 && chmod +x clair-scanner
//                        ./clair-scanner --ip="$DOCKER_GATEWAY" ${registry} || exit 0
//                    '''
//                }
//           }

            stage('Publish Docker Image') {
                steps {
                    script {
                        docker.withRegistry( '', registryCredential ) {
                            dockerImage.push("${BUILD_NUMBER}")
                        }
                    }
                }
            }

            stage('Cleanup Workspace') {
                steps {
                    sh '''
                        docker rmi ${registry}:${BUILD_NUMBER}
                        sleep 15
                        docker container prune -f
                        docker image prune -f
                    '''
                }
            }
        }
        post {
            always {
                cleanWs()
            }
        }
    }
}
