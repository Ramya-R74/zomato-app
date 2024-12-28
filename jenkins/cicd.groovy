pipeline{
    agent any;
    
    stages{
        stage("Code Clone"){
            steps{
                echo "Code Clone Stage"
                git url: "https://github.com/Ramya-R74/zomato-app.git", branch: "main"
            }
        }
        stage("Code Build & Test"){
            steps{
                echo "Code Build Stage"
                sh "docker build -t zomato-app ."
            }
        }
        stage("Push To DockerHub"){
            steps{
                withCredentials([usernamePassword(
                    credentialsId:"dockerHubCreds",
                    usernameVariable:"dockerHubUser", 
                    passwordVariable:"dockerHubPass")]){
                sh 'echo $dockerHubPass | docker login -u $dockerHubUser --password-stdin'
                sh "docker image tag zomato-app:latest ${env.dockerHubUser}/zomato-app:latest"
                sh "docker push ${env.dockerHubUser}/zomato-app:latest"
                }
            }
        }
        stage("Deploy"){
            steps{
                sh "docker stop zomato-app || true && docker rm zomato-app || true"
                sh "docker run -d -p 3000:3000 --name zomato-app ${env.dockerHubUser}/zomato-app:latest"
            }
        }
    }
}
