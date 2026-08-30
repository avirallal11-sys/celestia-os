#!/bin/bash
# Celestia 1.0 - Wallpaper Generator
# Creates cosmic/nebula wallpapers using ImageMagick

set -e

WALLPAPER_DIR="$(dirname "$0")"
OUTPUT_DIR="$WALLPAPER_DIR"

echo "Generating Celestia wallpapers..."

# Check if ImageMagick is installed
if ! command -v convert &> /dev/null; then
    echo "ImageMagick is required. Install with: sudo apt install imagemagick"
    exit 1
fi

# Create main wallpaper (1080x1920 for phones)
echo "Creating main wallpaper (1080x1920)..."
convert -size 1080x1920 xc:"#0A0A1A" \
    \( -size 1080x1920 xc:none \
       -fill "rgba(0,212,255,0.15)" \
       -draw "circle 540,960 540,600" \
    \) -composite \
    \( -size 1080x1920 xc:none \
       -fill "rgba(100,0,255,0.1)" \
       -draw "circle 540,960 540,700" \
    \) -composite \
    \( -size 1080x1920 xc:none \
       -fill "rgba(255,0,100,0.05)" \
       -draw "circle 540,960 540,800" \
    \) -composite \
    \( -size 1080x1920 xc:none \
       -fill "white" \
       -draw "circle 540,960 540,940" \
    \) -composite \
    \( -size 1080x1920 xc:none \
       -fill "rgba(0,212,255,0.3)" \
       -draw "circle 540,960 540,950" \
    \) -composite \
    "$OUTPUT_DIR/celestia_wallpaper_phone.png"

echo "  Created: celestia_wallpaper_phone.png"

# Create tablet wallpaper (1600x2560)
echo "Creating tablet wallpaper (1600x2560)..."
convert -size 1600x2560 xc:"#0A0A1A" \
    \( -size 1600x2560 xc:none \
       -fill "rgba(0,212,255,0.15)" \
       -draw "circle 800,1280 800,800" \
    \) -composite \
    \( -size 1600x2560 xc:none \
       -fill "rgba(100,0,255,0.1)" \
       -draw "circle 800,1280 800,900" \
    \) -composite \
    \( -size 1600x2560 xc:none \
       -fill "rgba(255,0,100,0.05)" \
       -draw "circle 800,1280 800,1000" \
    \) -composite \
    \( -size 1600x2560 xc:none \
       -fill "white" \
       -draw "circle 800,1280 800,1260" \
    \) -composite \
    "$OUTPUT_DIR/celestia_wallpaper_tablet.png"

echo "  Created: celestia_wallpaper_tablet.png"

# Create PC wallpaper (1920x1080)
echo "Creating PC wallpaper (1920x1080)..."
convert -size 1920x1080 xc:"#0A0A1A" \
    \( -size 1920x1080 xc:none \
       -fill "rgba(0,212,255,0.15)" \
       -draw "circle 960,540 960,200" \
    \) -composite \
    \( -size 1920x1080 xc:none \
       -fill "rgba(100,0,255,0.1)" \
       -draw "circle 960,540 960,300" \
    \) -composite \
    \( -size 1920x1080 xc:none \
       -fill "rgba(255,0,100,0.05)" \
       -draw "circle 960,540 960,400" \
    \) -composite \
    \( -size 1920x1080 xc:none \
       -fill "white" \
       -draw "circle 960,540 960,520" \
    \) -composite \
    "$OUTPUT_DIR/celestia_wallpaper_pc.png"

echo "  Created: celestia_wallpaper_pc.png"

# Create boot logo (512x512)
echo "Creating boot logo (512x512)..."
convert -size 512x512 xc:"#0A0A1A" \
    \( -size 512x512 xc:none \
       -fill "rgba(0,212,255,0.2)" \
       -draw "circle 256,256 256,100" \
    \) -composite \
    \( -size 512x512 xc:none \
       -fill "white" \
       -draw "circle 256,256 256,240" \
    \) -composite \
    \( -size 512x512 xc:none \
       -fill "rgba(0,212,255,0.4)" \
       -draw "circle 256,256 256,248" \
    \) -composite \
    -font "DejaVu-Sans-Bold" \
    -pointsize 48 \
    -fill "#00D4FF" \
    -gravity center \
    -annotate +0+80 "CELESTIA" \
    "$OUTPUT_DIR/boot_logo.png"

echo "  Created: boot_logo.png"

echo ""
echo "All wallpapers generated!"
echo "Files saved to: $OUTPUT_DIR"
