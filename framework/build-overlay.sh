#!/bin/bash
# Celestia 1.0 - System Overlay Builder
# Creates the framework-res.apk overlay for system theming

set -e

OVERLAY_DIR="$(dirname "$0")"
BUILD_DIR="$OVERLAY_DIR/build"

echo "Building Celestia System Overlay..."

# Create build directory
mkdir -p "$BUILD_DIR"

# Copy overlay resources
echo "Copying overlay resources..."
cp -r "$OVERLAY_DIR/overlay/values" "$BUILD_DIR/"
cp -r "$OVERLAY_DIR/overlay/drawable" "$BUILD_DIR/" 2>/dev/null || true

# Create aapt2 compiled resources
echo "Compiling resources..."
if command -v aapt2 &> /dev/null; then
    aapt2 compile --dir "$BUILD_DIR/values" -o "$BUILD_DIR/compiled.zip"

    echo "Resources compiled successfully"
    echo ""
    echo "To create the final overlay APK:"
    echo "  1. Use Android Studio to create a new project"
    echo "  2. Copy the compiled resources"
    echo "  3. Build the APK with: ./gradlew assembleRelease"
    echo ""
    echo "Or use the Android build system:"
    echo "  1. Copy overlay directory to device/x86/celestia/overlay/"
    echo "  2. Add to device.mk: PRODUCT_PACKAGE_OVERLAYS += device/x86/celestia/overlay"
else
    echo "aapt2 not found. Using alternative method..."
    echo ""
    echo "To apply the overlay:"
    echo "  1. Copy values/colors.xml to frameworks/base/core/res/res/values/"
    echo "  2. Copy values/styles.xml to frameworks/base/core/res/res/values/"
    echo "  3. Rebuild the framework"
fi

echo ""
echo "Overlay build complete!"
