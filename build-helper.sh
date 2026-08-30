#!/bin/bash
# Celestia 1.0 - Quick Build Helper
# Use this to build individual components

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================="
echo "  Celestia 1.0 - Component Builder"
echo "=========================================="
echo ""
echo "Select what to build:"
echo "1. Generate boot animation frames"
echo "2. Build Celestia Camera APK"
echo "3. Build Celestia Settings APK"
echo "4. Build Celestia Launcher APK"
echo "5. Build all apps"
echo "6. Create bootanimation.zip"
echo "7. Full ISO build (requires Android-x86 source)"
echo ""
read -p "Enter choice (1-7): " choice

case $choice in
    1)
        echo "Generating boot animation..."
        bash "$SCRIPT_DIR/bootanimation/generate-frames.sh"
        ;;
    2)
        echo "Building Celestia Camera..."
        cd "$SCRIPT_DIR/apps/celestia-camera"
        if [ -f "gradlew" ]; then
            ./gradlew assembleDebug
        else
            echo "Note: Android build system required. Use Android Studio or AOSP build."
        fi
        ;;
    3)
        echo "Building Celestia Settings..."
        cd "$SCRIPT_DIR/apps/celestia-settings"
        if [ -f "gradlew" ]; then
            ./gradlew assembleDebug
        else
            echo "Note: Android build system required. Use Android Studio or AOSP build."
        fi
        ;;
    4)
        echo "Building Celestia Launcher..."
        cd "$SCRIPT_DIR/apps/celestia-launcher"
        if [ -f "gradlew" ]; then
            ./gradlew assembleDebug
        else
            echo "Note: Android build system required. Use Android Studio or AOSP build."
        fi
        ;;
    5)
        echo "Building all Celestia apps..."
        for app in celestia-camera celestia-settings celestia-launcher; do
            echo "Building $app..."
            cd "$SCRIPT_DIR/apps/$app"
            if [ -f "gradlew" ]; then
                ./gradlew assembleDebug
            fi
            cd "$SCRIPT_DIR"
        done
        echo "All apps built!"
        ;;
    6)
        echo "Creating bootanimation.zip..."
        cd "$SCRIPT_DIR/bootanimation"
        if [ -d "part0" ]; then
            zip -r ../build/bootanimation.zip desc.txt part*/
            echo "bootanimation.zip created in build/"
        else
            echo "Error: Run option 1 first to generate frames"
        fi
        ;;
    7)
        echo "Starting full ISO build..."
        bash "$SCRIPT_DIR/build-celestia.sh"
        ;;
    *)
        echo "Invalid choice"
        ;;
esac
