<script setup>
import { ref, watch, onMounted } from "vue";

var map;
const positions = ref([]);
const markers = ref([]);
const infoWindow = ref([]);

// const props = defineProps({ campingArea: Array, selectedOption: Object });
const props = defineProps({ campingData: Array, selectCampsite: Object });

watch(
    () => props.selectCampsite,
    () => {

        // 이동할 위도 경도 위치를 생성합니다
        var moveLatLon = new kakao.maps.LatLng(props.selectCampsite.mapY, props.selectCampsite.mapX);
        map.setLevel(3);
        console.log(props.selectCampsite);

        infoWindow.value.forEach(info => {
            info.setMap(null);
        });
        markers.value.forEach(m => {
            if (m.id == props.selectCampsite.contentId) {
                // 인포윈도우를 생성합니다
                var content = '<div class="wrap">' +
                    '    <div class="info">' +
                    '        <div class="title">' +
                    `            ${props.selectCampsite.facltNm}` +
                    '        </div>' +
                    '        <div class="body">' +
                    '            <div class="img">' +
                    `                <img src="${props.selectCampsite.firstImageUrl}" width="73" height="70">` +
                    '           </div>' +
                    '            <div class="desc">' +
                    `                <div class="ellipsis">${props.selectCampsite.addr1}</div>` +
                    `                <div class="jibun ellipsis">${props.selectCampsite.posblFcltyCl}</div>` +
                    '            </div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>';


                // 인포윈도우를 생성합니다
                var mapCustomOverlay = new kakao.maps.CustomOverlay({
                    content: content,
                    removable: true,
                    map: map,
                    position: m.getPosition()
                });
                infoWindow.value.push(mapCustomOverlay);
                mapCustomOverlay.setMap(map);
            }
        });
        // // 지도 중심을 부드럽게 이동시킵니다
        // // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
        map.panTo(moveLatLon);
    },
    { deep: true }
);

onMounted(() => {
    if (window.kakao && window.kakao.maps) {
        initMap();
    } else {
        const script = document.createElement("script");
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${import.meta.env.VITE_KAKAO_MAP_SERVICE_KEY
            }&libraries=services,clusterer`;
        /* global kakao */
        script.onload = () => kakao.maps.load(() => initMap());
        document.head.appendChild(script);
    }
});

watch(
    () => props.campingData.value,
    () => {
        positions.value = [];
        props.campingData.forEach((camp) => {
            let obj = {};
            obj.latlng = new kakao.maps.LatLng(camp.mapY, camp.mapX);
            obj.id = camp.contentId;
            obj.title = camp.facltNm;
            obj.info = camp.lineIntro;

            positions.value.push(obj);
        });
        loadMarkers();
    },
    { deep: true }
);

const initMap = () => {
    const container = document.getElementById("map");
    const options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 3,
    };
    map = new kakao.maps.Map(container, options);

    // loadMarkers();
};

const loadMarkers = () => {
    // 현재 표시되어있는 marker들이 있다면 map에 등록된 marker를 제거한다.
    deleteMarkers();

    // 마커 이미지를 생성합니다
    //   const imgSrc = require("@/assets/map/markerStar.png");
    // 마커 이미지의 이미지 크기 입니다
    //   const imgSize = new kakao.maps.Size(24, 35);
    //   const markerImage = new kakao.maps.MarkerImage(imgSrc, imgSize);

    // 마커를 생성합니다
    markers.value = [];
    positions.value.forEach((position) => {
        const marker = new kakao.maps.Marker({
            map: map, // 마커를 표시할 지도
            position: position.latlng, // 마커를 표시할 위치
            title: position.title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됨.
            clickable: true, // // 마커를 클릭했을 때 지도의 클릭 이벤트가 발생하지 않도록 설정합니다
            // image: markerImage, // 마커의 이미지
        });
        marker.id = position.id;
        markers.value.push(marker);

    });

    // 4. 지도를 이동시켜주기
    // 배열.reduce( (누적값, 현재값, 인덱스, 요소)=>{ return 결과값}, 초기값);
    const bounds = positions.value.reduce(
        (bounds, position) => bounds.extend(position.latlng),
        new kakao.maps.LatLngBounds()
    );

    map.setBounds(bounds);
};

const deleteMarkers = () => {
    if (markers.value.length > 0) {
        markers.value.forEach((marker) => marker.setMap(null));
    }
};


</script>

<template>
    <div id="map"></div>
</template>

<style>
#map {
    width: 100%;
    height: 700px;
}

.wrap {position: absolute;left: 0;bottom: 40px;width: 288px;height: 152px;margin-left: -144px;text-align: left;overflow: hidden;font-size: 12px;font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;line-height: 1.5;}
    .wrap * {padding: 0;margin: 0;}
    .wrap .info {width: 286px;height: 140px;border-radius: 5px;border-bottom: 2px solid #ccc;border-right: 1px solid #ccc;overflow: hidden;background: #fff;}
    .wrap .info:nth-child(1) {border: 0;box-shadow: 0px 1px 2px #888;}
    .info .title {padding: 5px 0 0 10px; height: 40px;background: #eee;border-bottom: 1px solid #ddd;font-size: 20px;font-weight: bold;}
    .info .body {position: relative;overflow: hidden;}
    .info .desc {position: relative;margin: 13px 0 0 90px;height: 75px;}
    .desc .ellipsis {overflow: hidden;text-overflow: ellipsis; white-space: wrap; font-size: 14px;}
    .desc .jibun {font-size: 14px;color: #888; margin-top: -2px; margin-right: 8px;}
    .info .img {position: absolute;top: 6px;left: 5px;width: 73px;height: 71px;border: 1px solid #ddd;color: #888;overflow: hidden;}
    .info:after {content: '';position: absolute;margin-left: -12px;left: 50%;bottom: 0;width: 22px;height: 12px;background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png')}
    .info .link {color: #5085BB;}
</style>
