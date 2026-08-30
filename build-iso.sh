#!/bin/bash
# Celestia 1.0 - Build Complete ISO
# This script assembles the final Android-x86 ISO with all customizations

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_X86_DIR="$SCRIPT_DIR/android-x86"
BUILD_DIR="$SCRIPT_DIR/build"
OUTPUT_DIR="$SCRIPT_DIR/output"

echo "=========================================="
echo "  Celestia 1.0 - Final ISO Build"
echo "=========================================="

# Check if Android-x86 source exists
if [ ! -d "$ANDROID_X86_DIR" ]; then
    echo "ERROR: Android-x86 source not found!"
    echo "Run: git clone --depth 1 -b android-11.0-r3 https://github.com/android-x86/android-x86.git"
    exit 1
fi

cd "$ANDROID_X86_DIR"

# Setup build environment
echo "Setting up build environment..."
source build/envsetup.sh

# Choose build target
echo "Select build target:"
echo "1. x86 (32-bit PC)"
echo "2. x86_64 (64-bit PC)"
echo "3. arm (ARM phones/tablets)"
read -p "Enter choice (1-3): " target

case $target in
    1)
        lunch android_x86-userdebug
        TARGET_ARCH="x86"
        ;;
    2)
        lunch android_x86_64-userdebug
        TARGET_ARCH="x86_64"
        ;;
    3)
        lunch android_arm-userdebug
        TARGET_ARCH="arm"
        ;;
    *)
        echo "Invalid choice, defaulting to x86_64"
        lunch android_x86_64-userdebug
        TARGET_ARCH="x86_64"
        ;;
esac

# Build the ISO
echo ""
echo "Building Celestia 1.0 ISO (this may take 1-3 hours)..."
echo "Target architecture: $TARGET_ARCH"
echo ""

# Use all available CPU cores
NUM_CORES=$(nproc)
echo "Using $NUM_CORES CPU cores for parallel build"

make -j"$NUM_CORES" iso_img

# Check if build succeeded
ISO_FILE=$(ls out/android_${TARGET_ARCH}-*.iso 2>/dev/null | head -1)

if [ -n "$ISO_FILE" ]; then
    # Create output directory
    mkdir -p "$OUTPUT_DIR"

    # Copy ISO
    cp "$ISO_FILE" "$OUTPUT_DIR/Celestia-1.0-${TARGET_ARCH}.iso"

    # Calculate checksum
    cd "$OUTPUT_DIR"
    sha256sum "Celestia-1.0-${TARGET_ARCH}.iso" > "Celestia-1.0-${TARGET_ARCH}.sha256"

    # Create release info
    cat > "Celestia-1.0-RELEASE.txt" << EOF
Celestia 1.0 - Custom Android 11 OS
====================================

Release: 1.0
Codename: Nebula
Base: Android 11 (AOSP)
Architecture: ${TARGET_ARCH}
Build Date: $(date)

Files:
  Celestia-1.0-${TARGET_ARCH}.iso    - Bootable ISO image
  Celestia-1.0-${TARGETARCH}.sha256   - SHA256 checksum

Features:
  - Custom boot animation (Rising Nebula)
  - Celestia Camera app
  - Celestia Settings app
  - Celestia Launcher (Home Screen)
  - Dark Cosmic theme
  - Custom wallpapers

Installation:
  1. Create bootable USB with Rufus/Etcher
  2. Boot from USB
  3. Follow installation wizard

Requirements:
  - x86/x86_64/ARM processor
  - 2GB RAM minimum (4GB recommended)
  - 8GB storage minimum

EOF

    echo ""
    echo "=========================================="
    echo "  BUILD SUCCESSFUL!"
    echo "=========================================="
    echo ""
    echo "ISO created at:"
    echo "  $OUTPUT_DIR/Celestia-1.0-${TARGET_ARCH}.iso"
    echo ""
    echo "Release files:"
    ls -lh "$OUTPUT_DIR/"
    echo ""
    echo "To test:"
    echo "  1. Create bootable USB with Rufus"
    echo "  2. Boot from USB"
    echo "  3. Or test in VirtualBox/VMware"
else
    echo "ERROR: Build failed!"
    echo "Check logs for details."
    exit 1
fi
