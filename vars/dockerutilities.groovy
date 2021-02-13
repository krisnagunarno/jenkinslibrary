#!/usr/bin/env groovy


def call(Map param){
        pipeline {

          agent {
            node 'manager-1'
          }

          stages {
            stage ('Test Pipeline') {
              steps {
                echo "Show Docker 1"
                sh '''
                  docker --version
                  docker image ls
                  echo "You can do docker-compose, and other command here!!"
                '''
                }
              }
               stage ('Docker Stack Deploy') {
                steps {
                  echo "Deploying Services"
                  script{
                    def files = findFiles(glob: '*.yml')
                    def command = "docker stack deploy "
                    files.each {
                      echo "${it.name}"
                      command += "-c ${it.name} "
                    }
                    command += "future"
                    echo "${command}"
                    sh (script: "${command}", returnStdout: true)
                }
              }
            }
          }
        }
}
