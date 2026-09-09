pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    environment {
        PROJECT = 'aiguibin-platform-arch'
        APP_JAR = 'aiguibin-platform-arch.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Frontend') {
            steps {
                dir('src/main/webapp') {
                    sh 'corepack enable || true'
                    sh 'pnpm install --frozen-lockfile'
                    sh 'pnpm run build'
                }
            }
        }

        stage('Build Backend') {
            steps {
                sh './mvnw -B -DskipTests clean package'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: "target/${APP_JAR}", fingerprint: true
            }
        }

        // TODO: 按需补充部署阶段（scp 到目标主机并执行 restart.sh）
    }

    post {
        always {
            echo '------- 构建结束 -------'
        }
        success {
            echo 'Build success'
        }
        failure {
            echo 'Build failed'
        }
    }
}
