pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
    }

    parameters {
        string(name: 'BASE_URI', defaultValue: 'https://practice.expandtesting.com/notes/api', description: 'Base URI of the Notes API under test')
    }

    environment {
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh '''
                    set -eu
                    java -version
                    javac -version
                    chmod +x ./gradlew
                '''
            }
        }

        stage('API Tests') {
            steps {
                sh '''
                    set -eu
                    ./gradlew clean test \
                      -DbaseUri="${BASE_URI:-https://practice.expandtesting.com/notes/api}" \
                      --no-daemon
                '''
            }
        }
    }

    post {
        always {
            junit testResults: 'build/test-results/test/*.xml', allowEmptyResults: true

            script {
                int allureReportStatus = sh(script: './gradlew allureReport --no-daemon', returnStatus: true)
                if (allureReportStatus != 0) {
                    echo 'Allure HTML report was not generated; Telegram notification can be skipped.'
                }
            }

            allure includeProperties: false,
                   jdk: '',
                   results: [[path: 'build/allure-results']]

            archiveArtifacts artifacts: 'build/reports/tests/test/**, build/allure-results/**, build/reports/allure-report/**',
                             allowEmptyArchive: true

            script {
                if (fileExists('build/reports/allure-report/allureReport/widgets/summary.json')) {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        String buildUrl = env.BUILD_URL?.trim() ?: ''
                        String allureReportLink = buildUrl.replaceAll('/+$', '').replaceAll('(/allure)+$', '') + '/allure'

                        withCredentials([
                                string(credentialsId: 'telegram-bot-token-rodneystone', variable: 'TELEGRAM_BOT_TOKEN'),
                                string(credentialsId: 'telegram-chat-id-rodneystone', variable: 'TELEGRAM_CHAT_ID')
                        ]) {
                            withEnv(["ALLURE_REPORT_LINK=${allureReportLink}", "API_BASE_URI=${params.BASE_URI}"]) {
                                sh '''
                                    set -eu
                                    cat > notifications/config-runtime.json <<EOF_JSON
{
  "base": {
    "project": "${JOB_BASE_NAME}",
    "environment": "${API_BASE_URI}",
    "comment": "Backend API autotests",
    "reportLink": "${ALLURE_REPORT_LINK}",
    "language": "ru",
    "allureFolder": "build/reports/allure-report/allureReport",
    "enableChart": true,
    "enableSuitesPublishing": false,
    "customData": {}
  },
  "telegram": {
    "token": "${TELEGRAM_BOT_TOKEN}",
    "chat": "${TELEGRAM_CHAT_ID}",
    "replyTo": "",
    "templatePath": "/templates/telegram.ftl"
  }
}
EOF_JSON

                                    java "-DconfigFile=notifications/config-runtime.json" \
                                      -jar notifications/allure-notifications-4.11.0.jar
                                '''
                            }
                        }
                    }
                } else {
                    echo 'Allure summary.json not found; Telegram notification skipped.'
                }
            }
        }
    }
}
