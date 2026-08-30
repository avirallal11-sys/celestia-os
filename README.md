# Celestia 1.0 - Custom Android-x86 OS

A custom Android 11-based operating system with a dark cosmic theme.

## Features
- **Custom Boot Animation**: Rising nebula/star animation
- **Celestia Camera**: Custom camera app with cosmic UI
- **Celestia Settings**: Custom settings app with dark theme
- **Celestia Launcher**: Custom home screen with cosmic design
- **System Theming**: Dark mode by default, custom icons, fonts

## Build Requirements
- WSL2 Ubuntu (recommended) or native Linux
- 8GB+ RAM (16GB recommended)
- 100GB+ free storage
- Android SDK & NDK

## Quick Start
```bash
# 1. Setup build environment
chmod +x setup-build-env.sh
./setup-build-env.sh

# 2. Build the OS
chmod +x build-celestia.sh
./build-celestia.sh
```

## Project Structure
```
celestia-os/
├── apps/
│   ├── celestia-camera/     # Custom camera app
│   ├── celestia-settings/   # Custom settings app
│   └── celestia-launcher/   # Custom home screen
├── bootanimation/           # Boot animation files
├── framework/               # System framework overlays
├── wallpaper/               # Default wallpapers
├── config/                  # Build configurations
└── build/                   # Build output
```

## Target Platforms
- Phone (ARM/x86)
- Tablet (ARM/x86)
- PC/Laptop (x86_64)

## Based On
- Android 11 (AOSP)
- Android-x86 Project
