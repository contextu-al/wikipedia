#!/bin/sh -x

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

echo "===== Setting Default Environment Variables ======"
APP_ENV="Prod"
APP_KEY="Wikipedia"
SDK_ENV="Dev"

GIT_VERSION=$(git log -1 --format="%h")
BUILD_TIME=$(date)

echo "===== Version of Contextual SDK installed ====="
./gradlew app:dependencies | grep pointzi

# Default is Develop using above environment variables
# Staging
echo "===== Build NewPipe .apk for AppCenter ====="
if [ "$GIT_BRANCH" = "staging" ]; then 
  APP_ENV="Staging"
  APP_KEY="Wikipedia_staging"
  ./gradlew assembleStaging
# Production
elif [ "$GIT_BRANCH" = "main" ]; then
  SDK_ENV='Prod'
  ./gradlew assembleRelease
fi

# We use lowercase variables as part of the Artifactory BDD path below
LOWERCASE_APP_ENV=$( tr '[A-Z]' '[a-z]' <<< $APP_ENV)
LOWERCASE_SDK_ENV=$( tr '[A-Z]' '[a-z]' <<< $SDK_ENV)



echo "===== Uploading .apk to AppCenter ====="
appcenter distribute release --app Contextual/Wikipedia-"$SDK_ENV"SDK-"$APP_ENV"-"$APP_KEY"-Android --file "app/build/outputs/apk/release/app-release.apk" --group "Collaborators"