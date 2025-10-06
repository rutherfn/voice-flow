#!/bin/bash

# VoiceFlow Library Publishing Script
# This script builds and publishes the VoiceFlow library to GitHub Packages

# Get version from core/gradle.properties
LIBRARY_VERSION=$(grep "LIBRARY_VERSION" core/gradle.properties | cut -d'=' -f2)
echo "🚀 Publishing VoiceFlow Library v${LIBRARY_VERSION} to GitHub Packages..."

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Check if core/gradle.properties exists with GitHub credentials
if ! grep -q "GITHUB_USERNAME" core/gradle.properties || ! grep -q "GITHUB_TOKEN" core/gradle.properties; then
    echo "❌ Error: core/gradle.properties file must contain GITHUB_USERNAME and GITHUB_TOKEN"
    exit 1
fi

# Clean and build the project
echo "📦 Cleaning and building the project..."
./gradlew clean

# Build the core module
echo "🔨 Building core module..."
./gradlew :core:assembleRelease

# Publish to GitHub Packages
echo "📤 Publishing to GitHub Packages..."
./gradlew :core:publish

if [ $? -eq 0 ]; then
    echo "✅ Successfully published VoiceFlow v${LIBRARY_VERSION} to GitHub Packages!"
    echo "📋 Library details:"
    echo "   - Group ID: com.nicholas.rutherford"
    echo "   - Artifact ID: voice-flow"
    echo "   - Version: ${LIBRARY_VERSION}"
    echo "   - Repository: https://maven.pkg.github.com/rutherfn/voice-flow"
    echo ""
    echo "🔗 To use this library in another project, add to your build.gradle:"
    echo "   repositories {"
    echo "       maven {"
    echo "           name = \"GitHubPackages\""
    echo "           url = uri(\"https://maven.pkg.github.com/rutherfn/voice-flow\")"
    echo "       }"
    echo "   }"
    echo ""
    echo "   dependencies {"
    echo "       implementation 'com.nicholas.rutherford:voice-flow:${LIBRARY_VERSION}'"
    echo "   }"
else
    echo "❌ Failed to publish library. Check the error messages above."
    exit 1
fi
