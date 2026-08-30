#!/bin/bash
# Celestia 1.0 - Main Build Script
# This script builds the custom Android-x86 OS

set -e

echo "=========================================="
echo "  Celestia 1.0 - Build Script"
echo "  Custom Android 11 OS"
echo "=========================================="

CELESTIA_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$CELESTIA_DIR/build"
ANDROID_X86_DIR="$CELESTIA_DIR/android-x86"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_step() {
    echo -e "${CYAN}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
print_step "Checking prerequisites..."

if ! command -v git &> /dev/null; then
    print_error "Git is required. Install with: sudo apt install git"
    exit 1
fi

if ! command -v java &> /dev/null; then
    print_error "Java is required. Install with: sudo apt install openjdk-11-jdk"
    exit 1
fi

print_success "Prerequisites check passed"

# Clone Android-x86 if not exists
if [ ! -d "$ANDROID_X86_DIR" ]; then
    print_step "Cloning Android-x86 source (android-11.0-r3)..."
    cd "$CELESTIA_DIR"
    git clone --depth 1 -b android-11.0-r3 https://github.com/android-x86/android-x86.git
    print_success "Android-x86 source cloned"
else
    print_success "Android-x86 source already exists"
fi

cd "$ANDROID_X86_DIR"

# Apply Celestia customizations
print_step "Applying Celestia 1.0 customizations..."

# Copy custom apps
print_step "Installing Celestia Camera..."
rm -rf packages/apps/CelestiaCamera
cp -r "$CELESTIA_DIR/apps/celestia-camera" packages/apps/CelestiaCamera

print_step "Installing Celestia Settings..."
rm -rf packages/apps/CelestiaSettings
cp -r "$CELESTIA_DIR/apps/celestia-settings" packages/apps/CelestiaSettings

print_step "Installing Celestia Launcher..."
rm -rf packages/apps/CelestiaLauncher
cp -r "$CELESTIA_DIR/apps/celestia-launcher" packages/apps/CelestiaLauncher

# Copy boot animation
print_step "Installing Celestia boot animation..."
rm -rf frameworks/base/data/bootanimation
mkdir -p frameworks/base/data/bootanimation
cp -r "$CELESTIA_DIR/bootanimation/"* frameworks/base/data/bootanimation/

# Apply system theming
print_step "Applying Celestia dark cosmic theme..."
if [ -f "$CELESTIA_DIR/framework/overlay/framework-res.apk" ]; then
    cp "$CELESTIA_DIR/framework/overlay/framework-res.apk" frameworks/base/core/res/res/
fi

# Copy wallpaper
print_step "Setting default wallpaper..."
if [ -f "$CELESTIA_DIR/wallpaper/celestia_wallpaper.png" ]; then
    cp "$CELESTIA_DIR/wallpaper/celestia_wallpaper.png" frameworks/base/core/res/res/drawable-nodpi/
fi

# Set build props
print_step "Configuring build properties..."
cat >> device/x86/ghost/BoardConfig.mk << 'EOF'

# Celestia OS Branding
PRODUCT_BRAND := Celestia
PRODUCT_MANUFACTURER := CelestiaOS
PRODUCT_MODEL := Celestia 1.0
PRODUCT_NAME := Celestia
EOF

print_success "Customizations applied"

# Setup build environment
print_step "Setting up build environment..."
source build/envsetup.sh

# Choose build target (x86 for PC, x86_64 for 64-bit PC)
print_step "Choosing build target..."
lunch android_x86_64-userdebug

# Build
print_step "Building Celestia 1.0 (this may take 1-3 hours)..."
make -j$(nproc) iso_img

# Check if build succeeded
if [ -f "out/android_x86_64-*.iso" ]; then
    ISO_FILE=$(ls out/android_x86_64-*.iso | head -1)
    cp "$ISO_FILE" "$BUILD_DIR/Celestia-1.0-x86_64.iso"
    print_success "Build complete!"
    echo ""
    echo -e "${GREEN}Celestia 1.0 ISO created at:${NC}"
    echo -e "  $BUILD_DIR/Celestia-1.0-x86_64.iso"
    echo ""
    echo -e "${CYAN}To test:${NC}"
    echo "  1. Create a bootable USB with Rufus"
    echo "  2. Boot from USB"
    echo "  3. Or test in VirtualBox/VMware"
else
    print_error "Build failed. Check logs for details."
    exit 1
fi
