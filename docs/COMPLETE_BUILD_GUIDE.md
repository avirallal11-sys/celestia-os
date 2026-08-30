# Celestia 1.0 - Complete Build Guide

## Prerequisites

### Windows Requirements
- WSL2 installed with Ubuntu
- At least 8GB RAM (16GB recommended)
- At least 50GB free storage
- Git installed

### Linux (WSL) Requirements
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y git-core gnupg flex bison build-essential zip curl \
    zlib1g-dev libc6-dev-i386 lib32ncurses-dev x11proto-core-dev \
    libx11-dev lib32z1-dev libgl1-mesa-dev libxml2-utils xsltproc \
    unzip fontconfig openjdk-11-jdk python3 python3-pip \
    libssl-dev rsync ccache lz4 imagemagick
```

## Step-by-Step Build Instructions

### 1. Setup Build Environment
```bash
cd ~/celestia-os
chmod +x setup-build-env.sh
./setup-build-env.sh
source ~/.bashrc
```

### 2. Clone Android-x86 Source
```bash
cd ~
git clone --depth 1 -b android-11.0-r3 https://github.com/android-x86/android-x86.git
cd android-x86
```

### 3. Apply Celestia Customizations
```bash
# Copy custom apps
cp -r ~/celestia-os/apps/celestia-camera packages/apps/CelestiaCamera
cp -r ~/celestia-os/apps/celestia-settings packages/apps/CelestiaSettings
cp -r ~/celestia-os/apps/celestia-launcher packages/apps/CelestiaLauncher

# Copy boot animation
cp -r ~/celestia-os/bootanimation/* frameworks/base/data/bootanimation/

# Copy system overlay
cp -r ~/celestia-os/framework/overlay/* device/x86/celestia/overlay/
```

### 4. Generate Wallpapers (Optional)
```bash
cd ~/celestia-os/wallpaper
chmod +x generate-wallpapers.sh
./generate-wallpapers.sh
```

### 5. Generate Boot Animation Frames (Optional)
```bash
cd ~/celestia-os/bootanimation
chmod +x generate-frames.sh
./generate-frames.sh
```

### 6. Build the ISO
```bash
cd ~/android-x86
source build/envsetup.sh
lunch android_x86_64-userdebug
make -j$(nproc) iso_img
```

### 7. Find the ISO
```bash
ls -la out/android_x86_64-*.iso
```

## Build Time Estimates
- First build: 2-4 hours
- Incremental builds: 10-30 minutes
- ISO generation: 5-10 minutes

## Troubleshooting

### Out of Memory
```bash
# Increase WSL2 memory in .wslconfig
# C:\Users\YourName\.wslconfig
[wsl2]
memory=8GB
swap=4GB
```

### Build Errors
```bash
# Clean and rebuild
make clean
make -j$(nproc) iso_img
```

### Missing Dependencies
```bash
sudo apt install -y $(cat build/workspace/install/Ubuntu/18.04/required.txt)
```

## Testing the ISO

### VirtualBox
1. Create new VM
2. Select ISO as boot media
3. Allocate 4GB RAM, 2 CPU cores
4. Start and install

### VMware
1. Create new VM
2. Select ISO
3. Customize settings
4. Install

### Physical Hardware
1. Download Rufus
2. Create bootable USB
3. Boot from USB
4. Install or run live
