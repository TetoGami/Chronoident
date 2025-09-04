# Chronoident APK Releases

This directory contains the built APK files for the Chronoident stopwatch application.

## Version 1.0 - ARM64 Support

### Release APK
- **File**: `chronoident-v1.0-arm64-release.apk`
- **Size**: ~4.4 MB
- **Architecture**: ARM64 (arm64-v8a)
- **Target SDK**: 33
- **Min SDK**: 24
- **Description**: Optimized release build ready for production deployment

### Debug APK  
- **File**: `chronoident-v1.0-arm64-debug.apk`
- **Size**: ~5.3 MB
- **Architecture**: ARM64 (arm64-v8a)
- **Target SDK**: 33
- **Min SDK**: 24
- **Description**: Debug build with debugging symbols for development testing

## Installation Instructions

1. Enable "Unknown Sources" in your Android device settings
2. Download the desired APK file
3. Tap the APK file to install
4. Grant necessary permissions when prompted

## Features

- Stopwatch functionality with start, stop, and reset controls
- Custom start time configuration
- Foreground service with notification controls
- Persistent timing across app lifecycle

## Compatibility

- **Android Version**: 7.0 (API 24) and above
- **Architecture**: ARM64/AArch64 devices
- **Permissions Required**: 
  - Foreground Service
  - Post Notifications

## Build Configuration

Built with:
- Android Gradle Plugin 7.4.2
- Gradle 7.6.3
- Kotlin 1.8.0
- Target SDK 33
- NDK ABI Filter: arm64-v8a