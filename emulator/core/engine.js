/**
 * Celestia OS Engine v1.0
 * Core operating system emulator
 */

class CelestiaEngine {
    constructor() {
        this.version = '1.0';
        this.codename = 'Nebula';
        this.currentScreen = 'boot';
        this.isBooted = false;
        this.isLocked = true;
        this.apps = {};
        this.notifications = [];
        this.settings = {
            brightness: 80,
            volume: 70,
            wifi: true,
            bluetooth: false,
            darkMode: true,
            wallpaper: 'nebula'
        };
        
        this.init();
    }

    init() {
        console.log(`%c Celestia OS ${this.version} (${this.codename}) `, 
            'background: #0A0A1A; color: #00D4FF; font-size: 16px; padding: 8px 16px;');
        console.log('%c Engine initialized ', 
            'background: #1A1A3E; color: #00D4FF; font-size: 12px; padding: 4px 8px;');
        
        this.boot();
        this.setupEventListeners();
        this.startTimeUpdate();
    }

    boot() {
        console.log('Booting Celestia OS...');
        
        // Boot sequence
        setTimeout(() => {
            this.showScreen('boot');
            this.updateBootProgress();
        }, 500);

        // After boot animation
        setTimeout(() => {
            this.showLockScreen();
        }, 4000);
    }

    updateBootProgress() {
        const progressBar = document.querySelector('.boot-progress-bar');
        if (progressBar) {
            progressBar.style.width = '100%';
        }
    }

    showScreen(screenId) {
        // Hide all screens
        document.querySelectorAll('.screen').forEach(screen => {
            screen.classList.remove('active');
        });

        // Show target screen
        const target = document.getElementById(`${screenId}-screen`);
        if (target) {
            target.classList.add('active');
            this.currentScreen = screenId;
        }
    }

    showLockScreen() {
        this.showScreen('lock');
        this.isLocked = true;
        this.updateLockTime();

        // Click to unlock
        const lockScreen = document.getElementById('lock-screen');
        lockScreen.onclick = () => this.unlock();
    }

    unlock() {
        if (!this.isLocked) return;
        
        this.isLocked = false;
        this.showHomeScreen();
    }

    showHomeScreen() {
        this.showScreen('home');
        this.isBooted = true;
        
        // Initialize launcher
        if (window.celestialLauncher) {
            window.celestialLauncher.init();
        }

        // Show notification after delay
        setTimeout(() => {
            this.showNotification('Celestia OS', 'System ready. Welcome to Celestia!');
        }, 1000);
    }

    setupEventListeners() {
        // Swipe down for notification panel
        let startY = 0;
        let isDragging = false;

        document.addEventListener('touchstart', (e) => {
            if (this.currentScreen !== 'home') return;
            startY = e.touches[0].clientY;
            isDragging = startY < 100;
        });

        document.addEventListener('touchmove', (e) => {
            if (!isDragging) return;
            const currentY = e.touches[0].clientY;
            if (currentY - startY > 50) {
                this.openNotificationPanel();
                isDragging = false;
            }
        });

        document.addEventListener('touchend', () => {
            isDragging = false;
        });

        // Mouse drag for desktop
        document.addEventListener('mousedown', (e) => {
            if (this.currentScreen !== 'home') return;
            startY = e.clientY;
            isDragging = startY < 100;
        });

        document.addEventListener('mousemove', (e) => {
            if (!isDragging) return;
            if (e.clientY - startY > 50) {
                this.openNotificationPanel();
                isDragging = false;
            }
        });

        document.addEventListener('mouseup', () => {
            isDragging = false;
        });

        // Keyboard shortcuts
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.closeAllPanels();
            }
            if (e.key === 'h') {
                this.showHomeScreen();
            }
        });
    }

    openNotificationPanel() {
        const panel = document.getElementById('notification-panel');
        panel.classList.add('active');
        document.getElementById('panel-time').textContent = this.getCurrentTime();
    }

    closeNotificationPanel() {
        const panel = document.getElementById('notification-panel');
        panel.classList.remove('active');
    }

    closeAllPanels() {
        this.closeNotificationPanel();
        this.closeModal();
        if (this.currentScreen !== 'home') {
            this.showHomeScreen();
        }
    }

    openApp(appId) {
        console.log(`Opening app: ${appId}`);
        
        const appContent = document.getElementById('app-content');
        const appTitle = document.getElementById('app-title');

        // Get app content
        const appData = window.celestialApps?.getApp(appId);
        if (appData) {
            appTitle.textContent = appData.name;
            appContent.innerHTML = appData.content;
            this.showScreen('app');
        }
    }

    closeApp() {
        this.showHomeScreen();
    }

    openSettings() {
        this.closeNotificationPanel();
        this.showScreen('settings');
        
        if (window.celestialApps) {
            window.celestialApps.initSettings();
        }
    }

    openCamera() {
        this.showScreen('camera');
    }

    openSearch() {
        // TODO: Implement search
        console.log('Search opened');
    }

    showModal() {
        document.getElementById('github-modal').classList.add('active');
    }

    closeModal() {
        document.getElementById('github-modal').classList.remove('active');
    }

    showNotification(title, message) {
        this.notifications.push({ title, message, time: new Date() });
        
        // Create notification element
        const notif = document.createElement('div');
        notif.className = 'toast-notification';
        notif.innerHTML = `
            <div class="toast-icon">&#9733;</div>
            <div class="toast-content">
                <strong>${title}</strong>
                <p>${message}</p>
            </div>
        `;
        notif.style.cssText = `
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: var(--bg-secondary);
            padding: 12px 20px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            gap: 12px;
            z-index: 300;
            animation: slideInUp 0.3s ease;
            border: 1px solid var(--accent);
        `;
        
        document.body.appendChild(notif);
        
        setTimeout(() => {
            notif.style.animation = 'slideOutDown 0.3s ease';
            setTimeout(() => notif.remove(), 300);
        }, 3000);
    }

    startTimeUpdate() {
        setInterval(() => {
            this.updateAllTimes();
        }, 1000);
    }

    updateAllTimes() {
        const time = this.getCurrentTime();
        const date = this.getCurrentDate();

        // Update all time displays
        const timeElements = ['lock-time', 'home-time', 'status-time'];
        timeElements.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = time;
        });

        // Update date displays
        const dateElements = ['lock-date', 'home-date'];
        dateElements.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = date;
        });
    }

    updateLockTime() {
        const lockTime = document.getElementById('lock-time');
        const lockDate = document.getElementById('lock-date');
        if (lockTime) lockTime.textContent = this.getCurrentTime();
        if (lockDate) lockDate.textContent = this.getCurrentDate();
    }

    getCurrentTime() {
        const now = new Date();
        return now.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit',
            hour12: false 
        });
    }

    getCurrentDate() {
        const now = new Date();
        return now.toLocaleDateString('en-US', { 
            weekday: 'long', 
            month: 'long', 
            day: 'numeric' 
        });
    }

    // System methods
    getSystemInfo() {
        return {
            os: `Celestia OS ${this.version}`,
            codename: this.codename,
            kernel: 'Linux 5.15.0-celestia',
            build: 'CELESTIA-1.0-20260829',
            model: 'Celestia Virtual Device',
            ram: '4 GB',
            storage: '32 GB',
            screen: `${window.innerWidth}x${window.innerHeight}`
        };
    }

    // GitHub sync
    async syncWithGitHub(repoUrl) {
        const status = document.getElementById('sync-status');
        status.innerHTML = '<div class="spinner"></div> Connecting to GitHub...';
        
        try {
            // Simulate GitHub API call
            await new Promise(resolve => setTimeout(resolve, 1500));
            status.innerHTML = '⬇️ Downloading updates...';
            
            await new Promise(resolve => setTimeout(resolve, 2000));
            status.innerHTML = '✅ Sync complete! System updated.';
            
            setTimeout(() => {
                this.closeModal();
                this.showNotification('GitHub Sync', 'Updates applied successfully!');
            }, 1500);
        } catch (error) {
            status.innerHTML = '❌ Sync failed. Check repo URL.';
        }
    }
}

// Initialize engine when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.celestiaEngine = new CelestiaEngine();
});

// Global functions
function closeApp() {
    window.celestiaEngine?.closeApp();
}

function openSettings() {
    window.celestiaEngine?.openSettings();
}

function openCamera() {
    window.celestiaEngine?.openCamera();
}

function openSearch() {
    window.celestiaEngine?.openSearch();
}

function closeModal() {
    window.celestiaEngine?.closeModal();
}

function syncRepo() {
    const url = document.getElementById('repo-url').value;
    window.celestiaEngine?.syncWithGitHub(url);
}

function closeNotificationPanel() {
    window.celestiaEngine?.closeNotificationPanel();
}
