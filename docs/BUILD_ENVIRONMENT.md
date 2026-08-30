# Celestia OS Build Environment

## Required Tools
- Git
- Java 11 (OpenJDK)
- Python 3
- Build essentials (gcc, g++, make)
- ccache (optional but recommended)
- ImageMagick (for boot animation)
- Android SDK & NDK

## WSL2 Setup
```bash
# Install WSL2 Ubuntu
wsl --install -d Ubuntu

# Update packages
sudo apt update && sudo apt upgrade

# Install build tools
sudo apt install git openjdk-11-jdk python3 build-essential \
    ccache imagemagick zip unzip curl wget
```

## Android SDK Setup
```bash
# Download Android SDK command line tools
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mv cmdline-tools latest

# Add to PATH
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# Accept licenses and install packages
sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3" "ndk;23.1.7779620"
```

## Disk Space Requirements
- Android-x86 source: ~25GB
- Build output: ~15GB
- Total needed: ~50GB minimum
- Recommended: 100GB+

## RAM Requirements
- Minimum: 8GB
- Recommended: 16GB
- For full AOSP build: 32GB

## Build Time Estimates
- First build: 2-4 hours
- Incremental builds: 10-30 minutes
- ISO generation: 5-10 minutes

## Performance Tips
1. Use ccache for faster rebuilds
2. Increase WSL2 memory in .wslconfig
3. Use SSD for build directory
4. Close other applications during build
