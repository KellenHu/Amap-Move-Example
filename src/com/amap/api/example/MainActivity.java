package com.amap.api.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

public class MainActivity extends Activity {

	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;
	private TextView text;
	int pointNum = 0;
	
	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static int TIME_INTERVAL = 60;
	private static final double DISTANCE = 0.000004;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.map);
		text = (TextView) findViewById(R.id.text);
		mMapView.onCreate(savedInstanceState);
		
		mAmap = mMapView.getMap();
		mAmap.getUiSettings().setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
		initRoadData();
		
		moveLooper();
	}
	
	double distance = 0.0;
	
	private void initRoadData() {
		// 116.504505 39.931057
		double centerLatitude = 39.916049;
		double centerLontitude = 116.399792;
		
/*		
		ArrayList<Points> points = new ArrayList<>();
		
		double a_x = 39.916049;
		double a_y = 116.399792;
		
		double b_x = 35.616049;
		double b_y = 120.339792;*/
		
		//我想写个循环求的这两个点之间的很多点
		
		PolylineOptions polylineOptions = new PolylineOptions();
		/*double deltaAngle = Math.PI / 180 * 5;
		double radius = 0.02;
		PolylineOptions polylineOptions = new PolylineOptions();
		for (double i = 0; i < Math.PI * 2; i = i + deltaAngle) {
			float latitude = (float) (-Math.cos(i) * radius + centerLatitude);
			float longtitude = (float) (Math.sin(i) * radius + centerLontitude);
			polylineOptions.add(new LatLng(latitude, longtitude));
			if (i > Math.PI) {
				deltaAngle = Math.PI / 180 * 30;
			}
			Log.e("TAG_POINT", "latitude-->" + latitude + "-----longtitude--》" + longtitude);
		}
		
		float latitude = (float) (-Math.cos(0) * radius + centerLatitude);
		float longtitude = (float) (Math.sin(0) * radius + centerLontitude);
		*/
		polylineOptions.add(new LatLng(centerLatitude, centerLontitude));
		 polylineOptions.add(new LatLng(39.528049, 117.589038));
		 distance = GetDistance(new LatLng(centerLatitude, centerLontitude),new LatLng(39.528049, 117.589038));
//		 TIME_INTERVAL = (int)(10/distance) * 10;
//		 polylineOptions.add(new LatLng(39.92515, 116.510997));
//		 polylineOptions.add(new LatLng(39.892198, 116.439449));
//		 polylineOptions.add(new LatLng(39.919324, 116.379367));
//		 polylineOptions.add(new LatLng(39.951704, 116.356708));
//		//
//		//
//		
//		//
//		 polylineOptions.add(new LatLng(39.970914, 116.408206));
//		
//		 polylineOptions.add(new LatLng(40.005899, 116.467601));
//		 polylineOptions.add(new LatLng(39.954368, 116.478038));
//		 polylineOptions.add(new LatLng(40.009368, 116.678038));
		polylineOptions.width(10);
		polylineOptions.color(Color.RED);
		
		mVirtureRoad = mAmap.addPolyline(polylineOptions);
		
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
		markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.marker));
		markerOptions.position(polylineOptions.getPoints().get(0));
		
		mMoveMarker = mAmap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) getAngle(0));

	}

	/**
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}

	/**
	 * 根据两点算取图标转的角度
	 */
	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

	/**
	 * 根据点和斜率算取截距
	 */
	private double getInterception(double slope, LatLng point) {

		double interception = point.latitude - slope * point.longitude;
		return interception;
	}
	 private static final double EARTH_RADIUS = 6378.137;//地球半径
	
	private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
	/**
     * 求两点经纬度之间的距离
     * @param startLatLng
     * @param endLatLng
     * @return
     */
    public double GetDistance(LatLng startLatLng,LatLng endLatLng)
    {

        double lat1 = startLatLng.latitude;
        double lng1 = startLatLng.longitude;

        double lat2 = endLatLng.latitude;
        double lng2 = endLatLng.longitude;

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000) / 10000;
//        double s = Math.sqrt((lat1 - lat2) * (lat1 - lat2) - (lng1 - lng2) * (lng1 - lng2));
        return s;
    }
	
	/**
	 * 算取斜率
	 */
	private double getSlope(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getSlope(startPoint, endPoint);
	}

	/**
	 * 算斜率
	 */
	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	/**
	 * 计算x方向每次移动的距离
	 */
	private double getXMoveDistance(double slope) {
		if (slope == Double.MAX_VALUE) {
			return DISTANCE;
		}
		return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			text.setText("耗时--->" + msg.what + "  距离---->" + distance * 1000 + "米"  + "点的个数--->" + pointNum);
			
		}
		
			
	};
	
	/**
	 * 循环进行移动逻辑
	 */
	public void moveLooper() {
		new Thread() {

			public void run() {
				while (true) {
					long nowLong = System.currentTimeMillis();
					pointNum = 0;
					for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {
						LatLng startPoint = mVirtureRoad.getPoints().get(i);
						LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
						mMoveMarker
						.setPosition(startPoint);

						mMoveMarker.setRotateAngle((float) getAngle(startPoint,
								endPoint));

						double slope = getSlope(startPoint, endPoint);
						//是不是正向的标示（向上设为正向）
						boolean isReverse = (startPoint.latitude > endPoint.latitude);

						double intercept = getInterception(slope, startPoint);

						double xMoveDistance = isReverse ? getXMoveDistance(slope)
								: -1 * getXMoveDistance(slope);

						
						for (double j = startPoint.latitude;
								!((j > endPoint.latitude)^ isReverse);
								
								j = j
								- xMoveDistance) {
							LatLng latLng = null;
							if (slope != Double.MAX_VALUE) {
								latLng = new LatLng(j, (j - intercept) / slope);
							} else {
								latLng = new LatLng(j, startPoint.longitude);
							}
							mMoveMarker.setPosition(latLng);
							try {
								Thread.sleep(TIME_INTERVAL);
								pointNum ++;
								handler.sendEmptyMessage((int)(System.currentTimeMillis()-nowLong));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

		}.start();
	}
}
