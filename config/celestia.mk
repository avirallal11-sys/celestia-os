# Celestia 1.0 - Build Configuration

# System Properties
PRODUCT_PROPERTY_OVERRIDES += \
    ro.build.display.id=Celestia-1.0 \
    ro.build.version.sdk=30 \
    ro.build.version.release=11 \
    ro.build.type=userdebug \
    ro.product.brand=Celestia \
    ro.product.manufacturer=CelestiaOS \
    ro.product.model=Celestia-1.0 \
    ro.product.name=Celestia

# Custom boot animation
PRODUCT_COPY_FILES += \
    frameworks/base/data/bootanimation/bootanimation.zip:system/media/bootanimation.zip

# Default wallpaper
PRODUCT_COPY_FILES += \
    device/x86/celestia/wallpaper/celestia_wallpaper.png:system/media/wallpaper/celestia_wallpaper.png

# System apps
PRODUCT_PACKAGES += \
    CelestiaCamera \
    CelestiaSettings \
    CelestiaLauncher

# Theming
PRODUCT_COPY_FILES += \
    device/x86/celestia/overlay/framework-res.apk:system/framework/framework-res.apk

# Fonts
PRODUCT_COPY_FILES += \
    device/x86/celestia/fonts/Roboto-Regular.ttf:system/fonts/Roboto-Regular.ttf \
    device/x86/celestia/fonts/Roboto-Bold.ttf:system/fonts/Roboto-Bold.ttf \
    device/x86/celestia/fonts/Roboto-Thin.ttf:system/fonts/Roboto-Thin.ttf

# Boot logo
PRODUCT_COPY_FILES += \
    device/x86/celestia/logo.png:system/media/logo.png
