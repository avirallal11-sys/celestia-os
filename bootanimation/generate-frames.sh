#!/bin/bash
# Generate Celestia boot animation frames using ImageMagick
# This creates the rising nebula/star animation

set -e

ANIM_DIR="$(dirname "$0")"
FRAMES_DIR="$ANIM_DIR/frames"
OUTPUT_DIR="$ANIM_DIR/part0"

echo "Generating Celestia boot animation frames..."

# Create directories
mkdir -p "$FRAMES_DIR"
mkdir -p "$OUTPUT_DIR"

# Generate 60 frames for the animation
TOTAL_FRAMES=60
WIDTH=480
HEIGHT=480

for i in $(seq 0 $((TOTAL_FRAMES - 1))); do
    FRAME_NUM=$(printf "%05d" $i)
    PROGRESS=$(echo "scale=2; $i / $TOTAL_FRAMES" | bc)

    # Calculate star size (grows over time)
    STAR_SIZE=$(echo "scale=0; 2 + ($PROGRESS * 30)" | bc)

    # Calculate nebula opacity (increases over time)
    OPACITY=$(echo "scale=0; $PROGRESS * 255" | bc 2>/dev/null || echo "200")

    # Generate frame with ImageMagick
    convert -size ${WIDTH}x${HEIGHT} xc:"#0A0A1A" \
        \( -size ${WIDTH}x${HEIGHT} xc:none \
           -fill "rgba(0,212,255,0.1)" \
           -draw "circle $((WIDTH/2)),$((HEIGHT/2)) $((WIDTH/2)),$((HEIGHT/2 + 50 + i*2))" \
        \) -composite \
        \( -size ${WIDTH}x${HEIGHT} xc:none \
           -fill "rgba(100,0,255,0.05)" \
           -draw "circle $((WIDTH/2)),$((HEIGHT/2)) $((WIDTH/2)),$((HEIGHT/2 + 30 + i*3))" \
        \) -composite \
        \( -size ${WIDTH}x${HEIGHT} xc:none \
           -fill "rgba(255,255,255,${OPACITY})" \
           -draw "circle $((WIDTH/2)),$((HEIGHT/2)) $((WIDTH/2)),$((HEIGHT/2 + STAR_SIZE))" \
        \) -composite \
        -fill "rgba(0,212,255,0.3)" \
        -draw "text $((WIDTH/2 - 60)),$((HEIGHT/2 + 80 + i)) 'CELESTIA'" \
        "$FRAMES_DIR/frame_${FRAME_NUM}.png"

    echo "  Generated frame $((i + 1))/$TOTAL_FRAMES"
done

# Convert frames to boot animation format
# For actual Android boot animation, we need PNG files in part directories
echo ""
echo "Converting to boot animation format..."

# Copy frames to part0
cp "$FRAMES_DIR"/*.png "$OUTPUT_DIR/"

echo "Boot animation frames generated in: $OUTPUT_DIR"
echo "Total frames: $TOTAL_FRAMES"
