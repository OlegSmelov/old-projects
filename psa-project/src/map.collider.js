(function() {
    var MapCollider = function(map) {
        this.map = map;
    };

    MapCollider.prototype.intersect_segment = function(point, segment_a, segment_b) {
        var min_y = Math.min(segment_a.y, segment_b.y);
        var max_y = Math.max(segment_a.y, segment_b.y);
        
        if (!(min_y <= point.y && point.y <= max_y)) {
            return false;
        }

        var projected_x = segment_a.x + (point.y - segment_a.y) /
            (segment_b.y - segment_a.y) * (segment_b.x - segment_a.x);

        return projected_x >= point.x;
    };

    // check if inside the map
    MapCollider.prototype.inside = function(point) {
        if (!(0 <= point.x && point.x <= this.map.width)) {
            return false;
        }
        if (!(0 <= point.y && point.y <= this.map.height)) {
            return false;
        }

        for (var polygon_id in this.map.polygons) {
            var polygon = this.map.polygons[polygon_id];
            var intersections = 0;
            for (var i = 0; i < polygon.length - 1; i++) {
                if (this.intersect_segment(point, polygon[i], polygon[i + 1])) {
                    intersections++;
                }
            }
            if (this.intersect_segment(point, polygon[0], polygon[polygon.length - 1])) {
                intersections++;
            }
            if (intersections % 2 == 1) {
                return false;
            }
        }
        return true;
    };

    if (typeof module !== 'undefined') {
        module.exports = MapCollider;
    } else {
        window.MapCollider = MapCollider;
    }
})();