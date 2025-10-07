# Voice Flow

A voice command library for Android that allows you to easily integrate voice phrase detection and command handling into your applications.

## Installation

1. Build the library:
   ```bash
   ./gradlew :core:assembleRelease
   ```

2. Copy the AAR file from `core/build/outputs/aar/core-release.aar` to your project's `libs` directory

3. Add the dependency to your app's `build.gradle`:
   ```gradle
   dependencies {
       implementation files('../libs/voice-flow-core.aar')  // if libs is in project root
       // OR
       implementation files('libs/voice-flow-core.aar')     // if libs is in app module
   }
   ```

## Usage

[Add your usage examples here]

## License

MIT License - see LICENSE file for details.