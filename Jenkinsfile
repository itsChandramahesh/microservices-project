pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = "1"
    }

    stages {

        // 🔹 1. Clone
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/itsChandramahesh/microservices-project'
            }
        }

        // 🔹 2. Build Backend
        stage('Build Spring Boot Services') {
            steps {
                sh '''
                cd user-service && mvn clean package -DskipTests
                cd ../product-service && mvn clean package -DskipTests
                cd ../order-service && mvn clean package -DskipTests
                '''
            }
        }

        // 🔹 3. Docker Compose Test
        stage('Docker Compose Test') {
            steps {
                sh '''
                docker-compose down || true
                docker-compose up -d --build
                sleep 20
                docker-compose ps
                docker-compose down
                '''
            }
        }

        // 🔥 4. Start Minikube
        stage('Start Minikube') {
            steps {
                sh '''
                echo "Starting Minikube..."

                if ! minikube status | grep -q "Running"; then
                    minikube start --driver=docker
                fi

                kubectl config use-context minikube
                kubectl get nodes
                '''
            }
        }

        // 🔥 5. Build Images INSIDE Minikube
        stage('Build Images in Minikube') {
            steps {
                sh '''
                echo "Switching to Minikube Docker..."

                eval $(minikube docker-env)

                docker build -t user-service ./user-service
                docker build -t product-service ./product-service
                docker build -t order-service ./order-service

                docker build -t payment-service ./payment-service
                docker build -t notification-service ./notification-service
                docker build -t analytics-service ./analytics-service

                docker build -t frontend ./order-frontend

                docker images
                '''
            }
        }

        // 🔹 6. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/

                sleep 15
                '''
            }
        }

        // 🔹 7. Verify
        stage('Verify Deployment') {
            steps {
                sh '''
                kubectl get pods
                kubectl get svc
                '''
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD Pipeline Completed Successfully!'
        }
        failure {
            echo '❌ Pipeline Failed. Check logs.'
        }
    }
}