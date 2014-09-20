// Based on
// http://www.html5rocks.com/en/tutorials/games/assetmanager/

function AssetManager() {
    this.downloadQueue = [];
    this.cache = {};
    this.successCount = 0;
    this.errorCount = 0;
}

AssetManager.prototype.queueDownload = function(path) {
    this.downloadQueue.push(path);
};

AssetManager.prototype.downloadAll = function(downloadCallback) {
    this.successCount = 0;
    this.errorCount = 0;

    if (downloadCallback && this.downloadQueue.length === 0) {
        downloadCallback();
    }

    for (var i = 0; i < this.downloadQueue.length; i++) {
        var path = this.downloadQueue[i];

        var assetManager = this;
        var successCallback = function() {
            assetManager.successCount++;
            if (downloadCallback && assetManager.isDone()) {
                downloadCallback();
            }
        };
        var errorCallback = function() {
            assetManager.errorCount++;
            if (downloadCallback && assetManager.isDone()) {
                downloadCallback();
            }
        };
        
        var extension = path.substring(path.length - 4, path.length).
            toUpperCase();
        if (extension == ".JPG" || extension == ".PNG") {
            var asset = new Image();
            asset.src = path;
            asset.addEventListener("load", successCallback, false);
        } else if (extension == ".MP3") {
            var asset = document.createElement("audio");
            asset.src = path;
            asset.addEventListener("loadeddata", successCallback, false);
        } else {
            console.error("Unknown asset type: " + path);
            this.errorCount++;
            continue;
        }

        asset.addEventListener("error", errorCallback, false);

        this.cache[path] = asset;
    }
};

AssetManager.prototype.isDone = function() {
    return this.downloadQueue.length == this.successCount + this.errorCount;
};

AssetManager.prototype.getAsset = function(path) {
    if (!this.cache[path]) {
        console.error("Asset not loaded: " + path);
        return null;
    }
    return this.cache[path];
}

// List of assets
var assetManager = new AssetManager();

// Audio
assetManager.queueDownload("/assets/sounds/attack.mp3");
assetManager.queueDownload("/assets/sounds/run.mp3");
assetManager.queueDownload("/assets/sounds/damage.mp3");
assetManager.queueDownload("/assets/sounds/bgm.mp3");

// Images
assetManager.queueDownload("/assets/sprites/swordnboard/knight_idle.png");
assetManager.queueDownload("/assets/sprites/swordnboard/knight_run.png");
assetManager.queueDownload("/assets/sprites/swordnboard/knight_attack.png");
assetManager.queueDownload("/assets/sprites/swordnboard/knight_get_damaged.png");

assetManager.queueDownload("/assets/sprites/swordnsword/knight_idle.png");
assetManager.queueDownload("/assets/sprites/swordnsword/knight_run.png");
assetManager.queueDownload("/assets/sprites/swordnsword/knight_attack.png");
assetManager.queueDownload("/assets/sprites/swordnsword/knight_get_damaged.png");

assetManager.queueDownload("/assets/sprites/shieldnshield/knight_idle.png");
assetManager.queueDownload("/assets/sprites/shieldnshield/knight_run.png");
assetManager.queueDownload("/assets/sprites/shieldnshield/knight_attack.png");
assetManager.queueDownload("/assets/sprites/shieldnshield/knight_get_damaged.png");

assetManager.queueDownload("/assets/sprites/spear/knight_idle.png");
assetManager.queueDownload("/assets/sprites/spear/knight_run.png");
assetManager.queueDownload("/assets/sprites/spear/knight_attack.png");
assetManager.queueDownload("/assets/sprites/spear/knight_get_damaged.png");

assetManager.queueDownload("/assets/sprites/twohandsword/knight_idle.png");
assetManager.queueDownload("/assets/sprites/twohandsword/knight_run.png");
assetManager.queueDownload("/assets/sprites/twohandsword/knight_attack.png");
assetManager.queueDownload("/assets/sprites/twohandsword/knight_get_damaged.png");

// Maps
assetManager.queueDownload("/assets/maps/towers.png");
assetManager.queueDownload("/assets/maps/aim.png");
assetManager.queueDownload("/assets/maps/battle.png");
assetManager.queueDownload("/assets/maps/clouds.png");
assetManager.queueDownload("/assets/maps/dune.png");
assetManager.queueDownload("/assets/maps/ontheedge.png");
assetManager.queueDownload("/assets/maps/riverside.png");
assetManager.queueDownload("/assets/maps/one.jpg");
assetManager.queueDownload("/assets/maps/two.jpg");
assetManager.queueDownload("/assets/maps/three.jpg");

assetManager.downloadAll();