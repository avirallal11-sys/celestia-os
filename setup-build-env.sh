#!/bin/bash
# Celestia 1.0 - Build Environment Setup Script
# Run this on WSL2 Ubuntu or native Linux

set -e

echo "=========================================="
echo "  Celestia 1.0 - Build Environment Setup"
echo "=========================================="

# Update system
echo "[1/8] Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install essential build tools
echo "[2/8] Installing build essentials..."
sudo apt install -y \
    git-core gnupg flex bison build-essential zip curl \
    zlib1g-dev libc6-dev-i386 lib32ncurses-dev x11proto-core-dev \
    libx11-dev lib32z1-dev libgl1-mesa-dev libxml2-utils xsltproc \
    unzip fontconfig openjdk-11-jdk python3 python3-pip \
    libssl-dev rsync ccache lz4pngquant imagemagick \
    libfuse-dev genisoimage mtools p7zip-full

# Install Java 11 (required for Android builds)
echo "[3/8] Setting up Java 11..."
sudo apt install -y openjdk-11-jdk
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Configure ccache for faster rebuilds
echo "[4/8] Configuring ccache..."
ccache -M 50G
echo 'export USE_CCACHE=1' >> ~/.bashrc
echo 'export CCACHE_DIR=~/.ccache' >> ~/.bashrc

# Install Android SDK command-line tools
echo "[5/8] Installing Android SDK..."
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
if [ ! -d "latest" ]; then
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip
    unzip -q cmdline-tools.zip
    mv cmdline-tools latest
    rm cmdline-tools.zip
fi

export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept Android SDK licenses
echo "[6/8] Accepting SDK licenses..."
yes | sdkmanager --licenses > /dev/null 2>&1 || true

# Install required SDK packages
echo "[7/8] Installing SDK packages..."
sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3" "ndk;23.1.7779620"

# Clone Android-x86 source (Android 11)
echo "[8/8] Cloning Android-x86 source..."
CELESTIA_DIR=~/celestia-os
mkdir -p $CELESTIA_DIR
cd $CELESTIA_DIR

if [ ! -d "android-x86" ]; then
    echo "Cloning android-x86 android-11.0-r3 branch..."
    git clone --depth 1 -b android-11.0-r3 https://github.com/android-x86/android-x86.git
fi

# Setup build environment variables
cat >> ~/.bashrc << 'EOF'

# Celestia OS Build Environment
export CELESTIA_HOME=~/celestia-os
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export USE_CCACHE=1
export CCACHE_DIR=~/.ccache
EOF

echo ""
echo "=========================================="
echo "  Build environment setup complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "  1. Run: source ~/.bashrc"
echo "  2. Run: ./build-celestia.sh"
echo ""
