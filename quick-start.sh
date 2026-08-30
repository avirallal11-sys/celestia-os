#!/bin/bash
# Celestia 1.0 - Quick Start Script
# Run this to set up everything automatically

set -e

echo "=========================================="
echo "  Celestia 1.0 - Quick Start"
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CELESTIA_HOME="$SCRIPT_DIR"

# Step 1: Update system
echo "[1/6] Updating system..."
sudo apt update && sudo apt upgrade -y

# Step 2: Install build dependencies
echo "[2/6] Installing build dependencies..."
sudo apt install -y \
    git-core gnupg flex bison build-essential zip curl \
    zlib1g-dev libc6-dev-i386 lib32ncurses-dev x11proto-core-dev \
    libx11-dev lib32z1-dev libgl1-mesa-dev libxml2-utils xsltproc \
    unzip fontconfig openjdk-11-jdk python3 python3-pip \
    libssl-dev rsync ccache lz4 imagemagick

# Step 3: Setup Java
echo "[3/6] Setting up Java 11..."
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Step 4: Configure ccache
echo "[4/6] Configuring ccache..."
ccache -M 50G
echo 'export USE_CCACHE=1' >> ~/.bashrc
echo 'export CCACHE_DIR=~/.ccache' >> ~/.bashrc

# Step 5: Clone Android-x86 source
echo "[5/6] Cloning Android-x86 source (android-11.0-r3)..."
if [ ! -d "$HOME/android-x86" ]; then
    cd "$HOME"
    git clone --depth 1 -b android-11.0-r3 https://github.com/android-x86/android-x86.git
else
    echo "  Android-x86 source already exists"
fi

# Step 6: Apply Celestia customizations
echo "[6/6] Applying Celestia customizations..."
cd "$HOME/android-x86"

# Copy custom apps
rm -rf packages/apps/CelestiaCamera
cp -r "$CELESTIA_HOME/apps/celestia-camera" packages/apps/CelestiaCamera

rm -rf packages/apps/CelestiaSettings
cp -r "$CELESTIA_HOME/apps/celestia-settings" packages/apps/CelestiaSettings

rm -rf packages/apps/CelestiaLauncher
cp -r "$CELESTIA_HOME/apps/celestia-launcher" packages/apps/CelestiaLauncher

# Copy boot animation
rm -rf frameworks/base/data/bootanimation
mkdir -p frameworks/base/data/bootanimation
cp -r "$CELESTIA_HOME/bootanimation/"* frameworks/base/data/bootanimation/

echo ""
echo "=========================================="
echo "  Setup Complete!"
echo "=========================================="
echo ""
echo "To build Celestia 1.0:"
echo "  cd ~/android-x86"
echo "  source build/envsetup.sh"
echo "  lunch android_x86_64-userdebug"
echo "  make -j\$(nproc) iso_img"
echo ""
echo "The ISO will be at:"
echo "  ~/android-x86/out/android_x86_64-*.iso"
echo ""
echo "Estimated build time: 2-4 hours"
echo ""
