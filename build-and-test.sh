#!/bin/bash

# VoiceFlow Library Build and Test Script
# This script builds and tests the VoiceFlow library

echo "🚀 Building VoiceFlow Library..."

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
    echo "   - AAR Location: core/build/outputs/aar/core-release.aar"
    echo "   - Version: 1.0.3"
    echo ""
    echo "🔗 To use this library in another project:"
    echo "   1. Copy the AAR file to your project's libs directory"
    echo "   2. Add dependency: implementation files('libs/voice-flow-core.aar')"
    echo ""
    echo "📝 For easy updates, use the update-aar.sh script"
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi
