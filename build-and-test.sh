#!/bin/bash

# VoiceFlow Library Build and Test Script
# This script builds and tests the VoiceFlow library for JitPack distribution

echo "🚀 Building VoiceFlow Library for JitPack..."

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Clean and build the project
echo "📦 Cleaning and building the project..."
./gradlew clean

# Build the core module
echo "🔨 Building core module..."
./gradlew :core:assembleRelease

# Test the build
echo "🧪 Testing the build..."
./gradlew :core:test

if [ $? -eq 0 ]; then
    echo "✅ Successfully built VoiceFlow library!"
    echo "📋 Library details:"
    echo "   - Group ID: com.github.rutherfn"
    echo "   - Artifact ID: voice-flow"
    echo "   - Version: 1.0.0"
    echo ""
    echo "🔗 To use this library in another project via JitPack:"
    echo "   repositories {"
    echo "       maven { url 'https://jitpack.io' }"
    echo "   }"
    echo ""
    echo "   dependencies {"
    echo "       implementation 'com.github.rutherfn:voice-flow:1.0.0'"
    echo "   }"
    echo ""
    echo "📝 To publish to JitPack:"
    echo "   1. Create a Git tag: git tag 1.0.0"
    echo "   2. Push the tag: git push origin 1.0.0"
    echo "   3. JitPack will automatically build and publish your library"
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi
