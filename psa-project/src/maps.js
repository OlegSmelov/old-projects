//used both in CLIENT and SERVER

(function() {
    var Map = function(polygons, background, spawn_one, spawn_two, width, height) {
        this.width = width || 960;
        this.height = height || 540;
        this.polygons = polygons;
        this.background = background;
        this.spawn_one = spawn_one;
        this.spawn_two = spawn_two;
    };

    var Maps = function() {};

    Maps.prototype.generateMaps = function() {
        var maps = [];
        var JSONMapStrings = [
            '{"width":960,"height":540,"polygons":[[{"x":130.5,"y":466.5},{"x":132.5,"y":64.5},{"x":83.5,"y":91.5},{"x":87.5,"y":165.5},{"x":85.5,"y":251.5},{"x":80.5,"y":301.5},{"x":86.5,"y":381.5},{"x":87.5,"y":433.5},{"x":92.5,"y":473.5},{"x":112.5,"y":503.5},{"x":230.5,"y":496.5},{"x":523.5,"y":514.5},{"x":819.5,"y":504.5},{"x":814.5,"y":464.5}],[{"x":804.5,"y":91.5},{"x":813.5,"y":517.5},{"x":849.5,"y":513.5},{"x":844.5,"y":43.5},{"x":138.5,"y":43.5},{"x":124.5,"y":99.5}],[{"x":231.5,"y":178.5},{"x":228.5,"y":342.5},{"x":276.5,"y":369.5},{"x":649.5,"y":370.5},{"x":702.5,"y":332.5},{"x":705.5,"y":220.5},{"x":689.5,"y":185.5},{"x":650.5,"y":187.5}]],"background":"/assets/maps/towers.png","spawn_one":{"x":182.5,"y":274.5},"spawn_two":{"x":746.5,"y":274.5}}',
            '{"width":960,"height":540,"polygons":[[{"x":110.5,"y":91.5},{"x":110.5,"y":217.5},{"x":321.5,"y":217.5},{"x":321.5,"y":90.5}],[{"x":605.5,"y":85.5},{"x":601.5,"y":214.5},{"x":817.5,"y":217.5},{"x":814.5,"y":87.5}],[{"x":612.5,"y":316.5},{"x":826.5,"y":315.5},{"x":828.5,"y":444.5},{"x":613.5,"y":448.5}],[{"x":325.5,"y":446.5},{"x":327.5,"y":318.5},{"x":114.5,"y":317.5},{"x":111.5,"y":446.5}]],"background":"/assets/maps/aim.png","spawn_one":{"x":59.5,"y":272.5},"spawn_two":{"x":887.5,"y":269.5}}',
            '{"width":960,"height":540,"polygons":[[{"x":-0.5,"y":234.5},{"x":958.5,"y":240.5},{"x":962.5,"y":253.5},{"x":0.5,"y":253.5}],[{"x":134.5,"y":299.5},{"x":187.5,"y":297.5},{"x":233.5,"y":301.5},{"x":278.5,"y":320.5},{"x":293.5,"y":343.5},{"x":288.5,"y":376.5},{"x":261.5,"y":395.5},{"x":212.5,"y":398.5},{"x":160.5,"y":398.5},{"x":143.5,"y":387.5},{"x":101.5,"y":384.5},{"x":92.5,"y":366.5},{"x":110.5,"y":334.5},{"x":116.5,"y":311.5}],[{"x":370.5,"y":456.5},{"x":364.5,"y":474.5},{"x":367.5,"y":492.5},{"x":379.5,"y":497.5},{"x":398.5,"y":494.5},{"x":413.5,"y":504.5},{"x":443.5,"y":511.5},{"x":473.5,"y":511.5},{"x":500.5,"y":508.5},{"x":525.5,"y":506.5},{"x":552.5,"y":498.5},{"x":566.5,"y":478.5},{"x":568.5,"y":451.5},{"x":556.5,"y":429.5},{"x":534.5,"y":418.5},{"x":505.5,"y":400.5},{"x":484.5,"y":409.5},{"x":450.5,"y":417.5},{"x":422.5,"y":420.5},{"x":400.5,"y":432.5},{"x":386.5,"y":439.5}],[{"x":603.5,"y":357.5},{"x":628.5,"y":371.5},{"x":659.5,"y":376.5},{"x":699.5,"y":368.5},{"x":730.5,"y":364.5},{"x":762.5,"y":349.5},{"x":797.5,"y":326.5},{"x":789.5,"y":297.5},{"x":768.5,"y":282.5},{"x":727.5,"y":280.5},{"x":670.5,"y":283.5},{"x":635.5,"y":293.5},{"x":614.5,"y":310.5},{"x":603.5,"y":334.5}]],"background":"/assets/maps/battle.png","spawn_one":{"x":66.5,"y":430.5},"spawn_two":{"x":867.5,"y":410.5}}',
            '{"width":960,"height":540,"polygons":[[{"x":73.5,"y":201.5},{"x":111.5,"y":246.5},{"x":204.5,"y":249.5},{"x":264.5,"y":271.5},{"x":303.5,"y":260.5},{"x":368.5,"y":260.5},{"x":385.5,"y":239.5},{"x":382.5,"y":219.5},{"x":363.5,"y":210.5},{"x":345.5,"y":199.5},{"x":321.5,"y":189.5},{"x":288.5,"y":190.5},{"x":237.5,"y":172.5},{"x":185.5,"y":158.5},{"x":146.5,"y":164.5},{"x":95.5,"y":171.5}],[{"x":413.5,"y":381.5},{"x":367.5,"y":405.5},{"x":324.5,"y":424.5},{"x":315.5,"y":446.5},{"x":337.5,"y":464.5},{"x":384.5,"y":479.5},{"x":424.5,"y":483.5},{"x":481.5,"y":481.5},{"x":528.5,"y":490.5},{"x":561.5,"y":494.5},{"x":604.5,"y":466.5},{"x":644.5,"y":476.5},{"x":671.5,"y":484.5},{"x":707.5,"y":484.5},{"x":742.5,"y":476.5},{"x":760.5,"y":466.5},{"x":762.5,"y":432.5},{"x":738.5,"y":407.5},{"x":682.5,"y":403.5},{"x":647.5,"y":400.5},{"x":594.5,"y":398.5},{"x":563.5,"y":409.5},{"x":493.5,"y":410.5},{"x":454.5,"y":406.5},{"x":434.5,"y":395.5}],[{"x":654.5,"y":199.5},{"x":668.5,"y":208.5},{"x":707.5,"y":226.5},{"x":752.5,"y":239.5},{"x":800.5,"y":241.5},{"x":853.5,"y":233.5},{"x":882.5,"y":224.5},{"x":899.5,"y":208.5},{"x":895.5,"y":183.5},{"x":919.5,"y":148.5},{"x":915.5,"y":128.5},{"x":880.5,"y":114.5},{"x":812.5,"y":138.5},{"x":761.5,"y":148.5},{"x":705.5,"y":147.5},{"x":675.5,"y":170.5}]],"background":"/assets/maps/clouds.png","spawn_one":{"x":148.4,"y":51.4},"spawn_two":{"x":827.4,"y":38.4}}',
            '{"width":960,"height":540,"polygons":[[{"x":120.5,"y":95.5},{"x":121.5,"y":117.5},{"x":89.5,"y":144.5},{"x":52.5,"y":186.5},{"x":44.5,"y":234.5},{"x":48.5,"y":265.5},{"x":52.5,"y":294.5},{"x":59.5,"y":320.5},{"x":76.5,"y":343.5},{"x":92.5,"y":365.5},{"x":109.5,"y":388.5},{"x":139.5,"y":413.5},{"x":171.5,"y":433.5},{"x":216.5,"y":452.5},{"x":264.5,"y":473.5},{"x":323.5,"y":485.5},{"x":400.5,"y":484.5},{"x":492.5,"y":486.5},{"x":619.5,"y":481.5},{"x":685.5,"y":469.5},{"x":771.5,"y":441.5},{"x":851.5,"y":412.5},{"x":899.5,"y":354.5},{"x":926.5,"y":278.5},{"x":934.5,"y":242.5},{"x":936.5,"y":194.5},{"x":928.5,"y":154.5},{"x":905.5,"y":132.5},{"x":847.5,"y":105.5},{"x":799.5,"y":86.5},{"x":751.5,"y":65.5},{"x":753.5,"y":45.5},{"x":942.5,"y":127.5},{"x":962.5,"y":211.5},{"x":893.5,"y":481.5},{"x":466.5,"y":524.5},{"x":15.5,"y":468.5},{"x":-0.5,"y":100.5}],[{"x":336.5,"y":422.5},{"x":299.5,"y":412.5},{"x":225.5,"y":379.5},{"x":178.5,"y":341.5},{"x":149.5,"y":295.5},{"x":142.5,"y":254.5},{"x":149.5,"y":231.5},{"x":163.5,"y":203.5},{"x":185.5,"y":176.5},{"x":207.5,"y":159.5},{"x":241.5,"y":142.5},{"x":282.5,"y":130.5},{"x":330.5,"y":113.5},{"x":383.5,"y":103.5},{"x":436.5,"y":96.5},{"x":509.5,"y":93.5},{"x":574.5,"y":81.5},{"x":628.5,"y":79.5},{"x":681.5,"y":85.5},{"x":714.5,"y":109.5},{"x":747.5,"y":122.5},{"x":780.5,"y":140.5},{"x":823.5,"y":168.5},{"x":851.5,"y":195.5},{"x":873.5,"y":221.5},{"x":877.5,"y":250.5},{"x":865.5,"y":284.5},{"x":839.5,"y":314.5},{"x":828.5,"y":305.5},{"x":837.5,"y":293.5},{"x":850.5,"y":276.5},{"x":862.5,"y":253.5},{"x":860.5,"y":229.5},{"x":848.5,"y":213.5},{"x":830.5,"y":184.5},{"x":802.5,"y":166.5},{"x":764.5,"y":145.5},{"x":732.5,"y":130.5},{"x":687.5,"y":113.5},{"x":650.5,"y":99.5},{"x":595.5,"y":98.5},{"x":519.5,"y":103.5},{"x":445.5,"y":108.5},{"x":377.5,"y":117.5},{"x":304.5,"y":134.5},{"x":257.5,"y":146.5},{"x":216.5,"y":166.5},{"x":177.5,"y":202.5},{"x":166.5,"y":222.5},{"x":160.5,"y":262.5},{"x":168.5,"y":305.5},{"x":199.5,"y":338.5},{"x":247.5,"y":376.5}],[{"x":297.5,"y":220.5},{"x":276.5,"y":229.5},{"x":255.5,"y":244.5},{"x":245.5,"y":268.5},{"x":249.5,"y":288.5},{"x":266.5,"y":302.5},{"x":285.5,"y":318.5},{"x":306.5,"y":337.5},{"x":335.5,"y":355.5},{"x":358.5,"y":362.5},{"x":394.5,"y":371.5},{"x":425.5,"y":374.5},{"x":458.5,"y":375.5},{"x":496.5,"y":371.5},{"x":535.5,"y":369.5},{"x":580.5,"y":354.5},{"x":617.5,"y":342.5},{"x":654.5,"y":327.5},{"x":676.5,"y":311.5},{"x":708.5,"y":287.5},{"x":708.5,"y":266.5},{"x":711.5,"y":232.5},{"x":706.5,"y":208.5},{"x":688.5,"y":179.5},{"x":665.5,"y":172.5},{"x":633.5,"y":164.5},{"x":594.5,"y":166.5},{"x":548.5,"y":169.5},{"x":516.5,"y":180.5},{"x":516.5,"y":197.5},{"x":535.5,"y":193.5},{"x":567.5,"y":186.5},{"x":610.5,"y":187.5},{"x":650.5,"y":189.5},{"x":680.5,"y":199.5},{"x":688.5,"y":222.5},{"x":691.5,"y":254.5},{"x":682.5,"y":279.5},{"x":670.5,"y":295.5},{"x":646.5,"y":312.5},{"x":618.5,"y":326.5},{"x":586.5,"y":339.5},{"x":549.5,"y":346.5},{"x":499.5,"y":355.5},{"x":454.5,"y":358.5},{"x":414.5,"y":353.5},{"x":368.5,"y":350.5},{"x":339.5,"y":331.5},{"x":297.5,"y":309.5},{"x":280.5,"y":287.5},{"x":268.5,"y":266.5},{"x":274.5,"y":249.5}]],"background":"/assets/maps/dune.png","spawn_one":{"x":102.5,"y":294.5},"spawn_two":{"x":515.5,"y":285.5}}',
            '{"width":960,"height":540,"polygons":[[{"x":1.5,"y":130.5},{"x":1.5,"y":199.5},{"x":961.5,"y":199.5},{"x":962.5,"y":157.5}],[{"x":2.5,"y":356.5},{"x":0.5,"y":320.5},{"x":962.5,"y":320.5},{"x":961.5,"y":349.5}]],"background":"/assets/maps/ontheedge.png","spawn_one":{"x":172.5,"y":261.5},"spawn_two":{"x":748.5,"y":264.5}}',
            '{"width":960,"height":540,"polygons":[[{"x":452.5,"y":2.5},{"x":365.5,"y":143.5},{"x":243.5,"y":148.5},{"x":221.5,"y":164.5},{"x":220.5,"y":178.5},{"x":245.5,"y":191.5},{"x":273.5,"y":199.5},{"x":306.5,"y":196.5},{"x":346.5,"y":196.5},{"x":391.5,"y":192.5},{"x":429.5,"y":190.5},{"x":462.5,"y":195.5},{"x":516.5,"y":206.5},{"x":572.5,"y":220.5},{"x":627.5,"y":215.5},{"x":655.5,"y":200.5},{"x":647.5,"y":175.5},{"x":614.5,"y":170.5},{"x":606.5,"y":157.5},{"x":624.5,"y":138.5},{"x":639.5,"y":114.5},{"x":644.5,"y":83.5},{"x":639.5,"y":63.5},{"x":637.5,"y":40.5},{"x":634.5,"y":14.5},{"x":636.5,"y":2.5}],[{"x":129.5,"y":397.5},{"x":151.5,"y":366.5},{"x":194.5,"y":344.5},{"x":225.5,"y":330.5},{"x":260.5,"y":319.5},{"x":296.5,"y":311.5},{"x":331.5,"y":307.5},{"x":385.5,"y":303.5},{"x":432.5,"y":309.5},{"x":468.5,"y":316.5},{"x":507.5,"y":323.5},{"x":545.5,"y":329.5},{"x":577.5,"y":349.5},{"x":614.5,"y":374.5},{"x":637.5,"y":395.5},{"x":663.5,"y":421.5},{"x":673.5,"y":436.5},{"x":637.5,"y":455.5},{"x":607.5,"y":447.5},{"x":577.5,"y":433.5},{"x":548.5,"y":433.5},{"x":527.5,"y":454.5},{"x":535.5,"y":495.5},{"x":545.5,"y":516.5},{"x":544.5,"y":532.5},{"x":215.5,"y":540.5},{"x":225.5,"y":511.5},{"x":240.5,"y":481.5},{"x":252.5,"y":454.5},{"x":266.5,"y":423.5},{"x":262.5,"y":398.5},{"x":257.5,"y":379.5},{"x":233.5,"y":378.5},{"x":191.5,"y":398.5},{"x":156.5,"y":416.5}]],"background":"/assets/maps/riverside.png","spawn_one":{"x":85.5,"y":267.5},"spawn_two":{"x":827.5,"y":303.5}}'
        ];

        for( JSONMapString_id in JSONMapStrings ){
            var JSONMapString = JSONMapStrings[ JSONMapString_id ];
            maps.push( JSON.parse( JSONMapString ) );
        }

        return maps;
    };

    if (typeof module !== 'undefined') {
        module.exports = Maps;
    } else {
        window.Map = Map;
        window.Maps = Maps;
    }
})();