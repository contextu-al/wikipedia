#!/bin/sh -x

if [ -z "$CONTEXTUAL_SDK_VERSION" ]; then
    git clone https://gitlab.com/contextual/sdks/android/contextual-sdk-android
    cd contextual-sdk-android
    git checkout $UPSTREAM_VERSION_NAME
    CONTEXTUAL_SDK_TAG=$(git describe --tags --abbrev=0)
    UPSTREAM_VERSION=${CONTEXTUAL_SDK_TAG}-${UPSTREAM_VERSION_NAME}
    cd ..
    echo "VERSION_NAME=${UPSTREAM_VERSION}" >> local.properties
    echo "Building ${UPSTREAM_VERSION} of SDK"
else
    echo "VERSION_NAME=${CONTEXTUAL_SDK_VERSION}" >> local.properties
    echo "Building ${CONTEXTUAL_SDK_VERSION} of SDK"
fi

echo "===== Ensuring correct language encoding and paths on build machine ====="
source ~/.profile

echo "===== Cleanup before fresh build ====="
rm -rf .app/build/outputs

echo "===== Determine git branch name ====="
if [ -z "$CI_COMMIT_REF_NAME" ]; then
    GIT_BRANCH=$(git symbolic-ref --short -q HEAD)
else
    GIT_BRANCH=$CI_COMMIT_REF_NAME
fi
APK_LOCATION=""

echo "===== Setting Default Environment Variables ======"
APP_ENV="Prod"
APP_KEY="Wikipedia"
SDK_ENV="Dev"

GIT_VERSION=$(git log -1 --format="%h")
BUILD_TIME=$(date)

./gradlew build --refresh-dependencies

echo "===== Build Wikipedia .apk for AppCenter ====="
# Default is Develop using above environment variables
# Staging
if [ "$GIT_BRANCH" = "staging" ]; then 
  APP_ENV="Staging"
  APP_KEY="Wikipedia_staging"
  ./gradlew assembleStagingDebug
  APK_LOCATION=app/build/outputs/apk/staging/debug/app-staging-debug.apk
# Production
elif [ "$GIT_BRANCH" = "main" ]; then
  SDK_ENV='Prod'
  ./gradlew assembleProd
  APK_LOCATION=app/build/outputs/apk/prod/debug/app-prod-debug.apk
elif [ "$GIT_BRANCH" = "develop" ]; then
  SDK_ENV='Dev'
  ./gradlew assembleContinuousIntegration
  APK_LOCATION=app/build/outputs/apk/dev/debug/app-dev-debug.apk
fi

# We use lowercase variables as part of the Artifactory BDD path below
LOWERCASE_APP_ENV=$( tr '[A-Z]' '[a-z]' <<< $APP_ENV)
LOWERCASE_SDK_ENV=$( tr '[A-Z]' '[a-z]' <<< $SDK_ENV)


echo "===== Uploading .apk to AppCenter ====="
appcenter distribute release --app Contextual/Wikipedia-"$SDK_ENV"SDK-"$APP_ENV"-"$APP_KEY"-Android --file "$APK_LOCATION" --group "Collaborators"