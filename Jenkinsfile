pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = "1"
    }

    stages {

        // 🔹 1. Clone
        stage('Clone Repository') {
            steps {
                git branch: 'V2', url: 'https://github.com/itsChandramahesh/microservices-project'
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
                if docker compose version >/dev/null 2>&1; then
                  COMPOSE_CMD="docker compose"
                else
                  COMPOSE_CMD="docker-compose"
                fi

                $COMPOSE_CMD down || true
                $COMPOSE_CMD up -d --build
                sleep 35
                $COMPOSE_CMD ps
                $COMPOSE_CMD down
                '''
            }
        }

        // 🔥 4. Start Minikube
        stage('Start Minikube') {
    steps {
        sh '''
        echo "🔧 Setting up Minikube..."

        # Clean broken cluster if exists
        minikube delete || true

        echo "🚀 Starting Minikube..."

        minikube start \
          --driver=docker \
          --memory=4096 \
          --cpus=2 \
          --kubernetes-version=v1.28.3 \
          --force

        echo "⏳ Waiting for Kubernetes API..."

        # Wait until node is ready
        kubectl wait --for=condition=Ready node/minikube --timeout=120s

        echo "📌 Setting context..."
        kubectl config use-context minikube

        echo "📊 Cluster status:"
        kubectl get nodes
        kubectl get pods -A
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

                kubectl rollout status deployment/postgres-db --timeout=180s
                kubectl rollout status deployment/user-service --timeout=180s
                kubectl rollout status deployment/product-service --timeout=180s
                kubectl rollout status deployment/order-service --timeout=180s
                kubectl rollout status deployment/payment-service --timeout=180s
                kubectl rollout status deployment/notification-service --timeout=180s
                kubectl rollout status deployment/analytics-service --timeout=180s
                kubectl rollout status deployment/frontend --timeout=180s
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
