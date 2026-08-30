/**
 * Celestia OS - Built-in Applications
 */

class CelestialApps {
    constructor() {
        this.apps = this.defineApps();
    }

    defineApps() {
        return {
            camera: {
                name: 'Celestia Camera',
                icon: '📸',
                color: '#1a1a3e',
                content: this.getCameraApp()
            },
            settings: {
                name: 'Settings',
                icon: '⚙️',
                color: '#1a1a3e',
                content: this.getSettingsApp()
            },
            files: {
                name: 'Files',
                icon: '📁',
                color: '#252545',
                content: this.getFilesApp()
            },
            calculator: {
                name: 'Calculator',
                icon: '🔢',
                color: '#1a1a3e',
                content: this.getCalculatorApp()
            },
            clock: {
                name: 'Clock',
                icon: '🕐',
                color: '#1a1a3e',
                content: this.getClockApp()
            },
            notes: {
                name: 'Notes',
                icon: '📝',
                color: '#252545',
                content: this.getNotesApp()
            },
            browser: {
                name: 'Browser',
                icon: '🌐',
                color: '#0f3460',
                content: this.getBrowserApp()
            },
            music: {
                name: 'Music',
                icon: '🎵',
                color: '#1a1a3e',
                content: this.getMusicApp()
            }
        };
    }

    getApp(appId) {
        return this.apps[appId] || null;
    }

    getCameraApp() {
        return `
            <div class="camera-app">
                <div class="camera-preview" style="height: 300px; position: relative; border-radius: 16px; overflow: hidden; background: linear-gradient(135deg, #1a1a2e, #16213e);">
                    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 80px; height: 80px; border: 2px solid #00D4FF; border-radius: 50; opacity: 0.7;"></div>
                    <div style="position: absolute; top: 16px; left: 16px; color: #00D4FF; font-weight: 600;">PHOTO</div>
                    <div style="position: absolute; top: 16px; right: 16px; color: white;">⚡</div>
                </div>
                <div style="display: flex; justify-content: center; gap: 40px; padding: 24px; background: rgba(0,0,0,0.8); border-radius: 0 0 16px 16px;">
                    <button style="width: 48px; height: 48px; border-radius: 50%; background: rgba(255,255,255,0.1); border: none; color: white; font-size: 20px; cursor: pointer;">↻</button>
                    <button id="camera-capture" style="width: 72px; height: 72px; border-radius: 50%; background: white; border: 4px solid #00D4FF; cursor: pointer; position: relative;">
                        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 60px; height: 60px; border-radius: 50%; background: white;"></div>
                    </button>
                    <button style="width: 48px; height: 48px; border-radius: 50%; background: rgba(255,255,255,0.1); border: none; color: white; font-size: 20px; cursor: pointer;">⚙</button>
                </div>
                <script>
                    document.getElementById('camera-capture')?.addEventListener('click', function() {
                        const flash = document.createElement('div');
                        flash.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:white;z-index:1000;animation:flash 0.2s ease;pointer-events:none;';
                        document.body.appendChild(flash);
                        setTimeout(() => flash.remove(), 200);
                    });
                </script>
            </div>
        `;
    }

    getSettingsApp() {
        return `
            <div class="settings-list">
                <div class="settings-item" onclick="window.celestiaEngine?.openSettings()">
                    <div class="settings-item-icon">📶</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Network & Internet</div>
                        <div class="settings-item-subtitle">Wi-Fi, mobile, data usage</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🔵</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Bluetooth</div>
                        <div class="settings-item-subtitle">Pair device, connections</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🖥️</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Display</div>
                        <div class="settings-item-subtitle">Brightness, wallpaper, sleep</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🔊</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Sound & Vibration</div>
                        <div class="settings-item-subtitle">Volume, Do Not Disturb</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🔋</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Battery</div>
                        <div class="settings-item-subtitle">100% - About 12 hr left</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">💾</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Storage</div>
                        <div class="settings-item-subtitle">32 GB total, 18 GB free</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🔒</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Privacy</div>
                        <div class="settings-item-subtitle">Permissions, activity controls</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">📍</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Location</div>
                        <div class="settings-item-subtitle">On - 3 apps have access</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">🛡️</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Security</div>
                        <div class="settings-item-subtitle">Screen lock, fingerprint</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">👤</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Accounts</div>
                        <div class="settings-item-subtitle">Celestia Account, Google</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">♿</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">Accessibility</div>
                        <div class="settings-item-subtitle">Screen readers, display</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">⚙️</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">System</div>
                        <div class="settings-item-subtitle">Language, gestures, backup</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item">
                    <div class="settings-item-icon">ℹ️</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title">About Celestia</div>
                        <div class="settings-item-subtitle">Device info, version 1.0</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
                <div class="settings-item" style="border-top: 1px solid rgba(255,255,255,0.1); margin-top: 8px; padding-top: 24px;">
                    <div class="settings-item-icon" style="color: #00D4FF;">⭐</div>
                    <div class="settings-item-text">
                        <div class="settings-item-title" style="color: #00D4FF;">Celestia Settings</div>
                        <div class="settings-item-subtitle">Custom themes, cosmic UI</div>
                    </div>
                    <div class="settings-item-arrow">›</div>
                </div>
            </div>
        `;
    }

    getFilesApp() {
        return `
            <div class="files-app">
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px;">
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">📷</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">DCIM</div>
                    </div>
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">📥</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">Download</div>
                    </div>
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">🎵</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">Music</div>
                    </div>
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">🎬</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">Videos</div>
                    </div>
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">📄</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">Documents</div>
                    </div>
                    <div style="text-align: center; padding: 20px; background: var(--bg-secondary); border-radius: 12px; cursor: pointer;">
                        <div style="font-size: 40px;">📦</div>
                        <div style="font-size: 13px; margin-top: 8px; color: var(--text-secondary);">Archives</div>
                    </div>
                </div>
            </div>
        `;
    }

    getCalculatorApp() {
        return `
            <div class="calculator-app" style="padding: 16px;">
                <div style="background: var(--bg-secondary); border-radius: 12px; padding: 24px; margin-bottom: 16px; text-align: right;">
                    <div id="calc-display" style="font-size: 36px; color: var(--accent);">0</div>
                </div>
                <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px;">
                    ${['C', '()', '%', '÷', '7', '8', '9', '×', '4', '5', '6', '-', '1', '2', '3', '+', '0', '.', '⌫', '='].map(btn => 
                        `<button onclick="calcPress('${btn}')" style="padding: 16px; border: none; border-radius: 8px; background: ${['÷','×','-','+','='].includes(btn) ? 'var(--accent)' : 'var(--bg-surface)'}; color: ${['='].includes(btn) ? 'var(--bg-primary)' : 'white'}; font-size: 18px; cursor: pointer;">${btn}</button>`
                    ).join('')}
                </div>
            </div>
        `;
    }

    getClockApp() {
        return `
            <div class="clock-app" style="text-align: center; padding: 40px 20px;">
                <div id="clock-big" style="font-size: 72px; font-weight: 200; color: var(--accent);">00:00:00</div>
                <div id="clock-full-date" style="font-size: 18px; color: var(--text-secondary); margin-top: 8px;">Saturday, August 29, 2026</div>
                <div style="margin-top: 40px; display: flex; justify-content: center; gap: 24px;">
                    <button style="padding: 12px 24px; background: var(--bg-secondary); border: 1px solid var(--accent); border-radius: 20px; color: var(--accent); cursor: pointer;">Alarm</button>
                    <button style="padding: 12px 24px; background: var(--bg-secondary); border: 1px solid var(--accent); border-radius: 20px; color: var(--accent); cursor: pointer;">Timer</button>
                    <button style="padding: 12px 24px; background: var(--accent); border: none; border-radius: 20px; color: var(--bg-primary); cursor: pointer;">Stopwatch</button>
                </div>
                <script>
                    setInterval(() => {
                        const now = new Date();
                        document.getElementById('clock-big').textContent = now.toLocaleTimeString();
                    }, 1000);
                </script>
            </div>
        `;
    }

    getNotesApp() {
        return `
            <div class="notes-app" style="padding: 16px;">
                <div style="background: var(--bg-secondary); border-radius: 12px; padding: 16px; margin-bottom: 12px;">
                    <div style="font-weight: 600; margin-bottom: 4px;">Welcome to Celestia</div>
                    <div style="font-size: 13px; color: var(--text-dim);">Your cosmic operating system awaits...</div>
                    <div style="font-size: 11px; color: var(--text-dim); margin-top: 8px;">Aug 29, 2026</div>
                </div>
                <div style="background: var(--bg-secondary); border-radius: 12px; padding: 16px; margin-bottom: 12px;">
                    <div style="font-weight: 600; margin-bottom: 4px;">Build Instructions</div>
                    <div style="font-size: 13px; color: var(--text-dim);">Run the build script to create your ISO</div>
                    <div style="font-size: 11px; color: var(--text-dim); margin-top: 8px;">Aug 29, 2026</div>
                </div>
                <button style="width: 100%; padding: 16px; background: var(--accent); border: none; border-radius: 12px; color: var(--bg-primary); font-size: 16px; font-weight: 600; cursor: pointer;">+ New Note</button>
            </div>
        `;
    }

    getBrowserApp() {
        return `
            <div class="browser-app">
                <div style="padding: 12px 16px; background: var(--bg-secondary); border-radius: 12px; margin: 16px; display: flex; align-items: center; gap: 12px;">
                    <span style="color: var(--text-dim);">🔒</span>
                    <input type="text" value="https://celestia-os.github.io" style="flex: 1; background: none; border: none; color: var(--text-primary); font-size: 14px; outline: none;">
                    <span style="color: var(--text-dim);">⟳</span>
                </div>
                <div style="padding: 40px; text-align: center;">
                    <div style="font-size: 48px; margin-bottom: 16px;">🌌</div>
                    <div style="font-size: 20px; color: var(--accent); margin-bottom: 8px;">Celestia Browser</div>
                    <div style="color: var(--text-dim);">Explore the cosmos</div>
                </div>
            </div>
        `;
    }

    getMusicApp() {
        return `
            <div class="music-app" style="padding: 24px; text-align: center;">
                <div style="width: 200px; height: 200px; margin: 0 auto; background: linear-gradient(135deg, var(--bg-secondary), var(--accent-dark)); border-radius: 20px; display: flex; align-items: center; justify-content: center; font-size: 80px;">🎵</div>
                <div style="margin-top: 24px;">
                    <div style="font-size: 20px; font-weight: 600;">Cosmic Dreams</div>
                    <div style="color: var(--text-dim); margin-top: 4px;">Celestia OST</div>
                </div>
                <div style="margin-top: 24px; padding: 0 20px;">
                    <div style="height: 4px; background: var(--bg-surface); border-radius: 2px;">
                        <div style="width: 35%; height: 100%; background: var(--accent); border-radius: 2px;"></div>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 12px; color: var(--text-dim);">
                        <span>1:23</span>
                        <span>3:45</span>
                    </div>
                </div>
                <div style="display: flex; justify-content: center; align-items: center; gap: 32px; margin-top: 24px;">
                    <button style="background: none; border: none; color: var(--text-dim); font-size: 24px; cursor: pointer;">⏮</button>
                    <button style="width: 64px; height: 64px; border-radius: 50%; background: var(--accent); border: none; color: var(--bg-primary); font-size: 24px; cursor: pointer;">▶</button>
                    <button style="background: none; border: none; color: var(--text-dim); font-size: 24px; cursor: pointer;">⏭</button>
                </div>
            </div>
        `;
    }

    initSettings() {
        // Settings initialization if needed
    }
}

// Initialize apps
document.addEventListener('DOMContentLoaded', () => {
    window.celestialApps = new CelestialApps();
});

// Calculator function
function calcPress(btn) {
    const display = document.getElementById('calc-display');
    if (!display) return;
    
    if (btn === 'C') {
        display.textContent = '0';
    } else if (btn === '⌫') {
        display.textContent = display.textContent.slice(0, -1) || '0';
    } else if (btn === '=') {
        try {
            display.textContent = eval(display.textContent.replace('×', '*').replace('÷', '/'));
        } catch {
            display.textContent = 'Error';
        }
    } else {
        if (display.textContent === '0') {
            display.textContent = btn;
        } else {
            display.textContent += btn;
        }
    }
}
