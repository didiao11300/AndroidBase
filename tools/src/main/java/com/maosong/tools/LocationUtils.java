package com.maosong.tools;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
 * 注意
 * 1、Android6.0动态权限,
 * 2、Geocoder获取地理位置信息是一个后台的耗时操作，
 * 为了不阻塞主线程，强力建议在使用Geocoder获取地理位置信息时采用异步线程的方式来请求服务，这样不会造成主线程的阻塞。
 * */
public class LocationUtils {

    private volatile static LocationUtils uniqueInstance;
    private LocationManager locationManager;
    private Context mContext;
    public static final String TAG = "LocationUtils";

    private LocationUtils(Context context) {
        mContext = context;
    }

    //采用Double CheckLock(DCL)实现单例
    public static LocationUtils getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils(context);
                }
            }
        }
        return uniqueInstance;
    }

    public LiveData<AddressBean> getLocation() {
        //1.获取位置管理器
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (null == locationManager)
            return null;
        //2.获取位置提供器，GPS或是NetWork
        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        String locationProvider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //GPS 定位的精准度比较高，但是非常耗电。
            LogUtil.i(TAG, "=====GPS_PROVIDER=====");
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {//Google服务被墙不可用
            //网络定位的精准度稍差，但耗电量比较少。
            LogUtil.i(TAG, "=====NETWORK_PROVIDER=====");
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            LogUtil.i(TAG, "=====NO_PROVIDER=====");
            // 当没有可用的位置提供器时，弹出Toast提示用户
//            Toast.makeText(mContext, "No location provider to use", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            mContext.startActivity(intent);
            return null;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        // FIXME: 2019/3/19 需要获取权限
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            // 显示当前设备的位置信息
            LogUtil.i(TAG, "==显示当前设备的位置信息==");
            return getAddressFormLocation(location);
        } else {//当GPS信号弱没获取到位置的时候可从网络获取
            LogUtil.i(TAG, "==Google服务被墙的解决办法==");
            return getLngAndLatWithNetwork();//Google服务被墙的解决办法
        }
        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
        //LocationManager 每隔 5 秒钟会检测一下位置的变化情况，当移动距离超过 10 米的时候，
        // 就会调用 LocationListener 的 onLocationChanged() 方法，并把新的位置信息作为参数传入。
//        locationManager.requestLocationUpdates(locationProvider, 5000, 10, locationListener);
    }

    //获取经纬度
    private LiveData<AddressBean> getAddressFormLocation(Location location) {
        if (null == location) {
            return null;
        }
        double latitude = location.getLatitude();//纬度
        double longitude = location.getLongitude();//经度
        return getAddress(latitude, longitude);
    }

    private LiveData<AddressBean> getAddress(double latitude, double longitude) {
        //Geocoder通过经纬度获取具体信息
        LogUtil.e(TAG, "getAddress   lat = " + latitude + "....long = " + longitude);
        Geocoder gc = new Geocoder(mContext, Locale.getDefault());
        MutableLiveData<AddressBean> addressData = new MutableLiveData<>();
        new Thread(() -> {
            try {
                List<Address> locationList = gc.getFromLocation(latitude, longitude, 1);
                if (locationList != null) {
                    Address address = locationList.get(0);
                    String countryName = address.getCountryName();//国家
                    String countryCode = address.getCountryCode();
                    String adminArea = address.getAdminArea();//省
                    String locality = address.getLocality();//市
                    String subAdminArea = address.getSubAdminArea();//区
                    String featureName = address.getFeatureName();//街道

                    for (int i = 0; address.getAddressLine(i) != null; i++) {
                        String addressLine = address.getAddressLine(i);
                        //街道名称:广东省深圳市罗湖区蔡屋围一街深圳瑞吉酒店
                        System.out.println("addressLine=====" + addressLine);
                    }

                    String currentPosition = "latitude is " + latitude//22.545975
                            + "\n" + "longitude is " + longitude//114.101232
                            + "\n" + "countryName is " + countryName//null
                            + "\n" + "countryCode is " + countryCode//CN
                            + "\n" + "adminArea is " + adminArea//广东省
                            + "\n" + "locality is " + locality//深圳市
                            + "\n" + "subAdminArea is " + subAdminArea//null
                            + "\n" + "featureName is " + featureName;//蔡屋围一街深圳瑞吉酒店
                    LogUtil.e("定位地址 = " + currentPosition);
                    AddressBean ab = new AddressBean(countryName, countryCode, adminArea, locality);
                    ab.latitude = latitude;
                    ab.longitude = longitude;
                    addressData.postValue(ab);
                } else {
                    AddressBean ab = new AddressBean(null, null, null, null);
                    ab.latitude = latitude;
                    ab.longitude = longitude;
                    addressData.postValue(ab);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return addressData;
    }

    public void removeLocationUpdatesListener() {
        if (locationManager != null) {
            uniqueInstance = null;
            locationManager.removeUpdates(locationListener);
        }
    }


    public class AddressBean {
        public String countryName;
        public String countryCode;
        public String area; //省
        public String locality;//市

        public double latitude;
        public double longitude;

        AddressBean(String countryName, String countryCode, String area, String locality) {
            this.countryName = countryName;
            this.countryCode = countryCode;
            this.area = area;
            this.locality = locality;
        }

        @Override
        public String toString() {
            return ("coutryName:" + countryName +
                    " countryCode:" + countryCode +
                    " area:" + area +
                    " city:" + longitude +
                    " latitude:" + latitude +
                    " longtude:" + longitude);
        }
    }


    private LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            System.out.println("==onLocationChanged==");
            getAddressFormLocation(location);
        }
    };

    //从网络获取经纬度
    private LiveData<AddressBean> getLngAndLatWithNetwork() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (null == locationManager) {
            return null;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return getAddressFormLocation(location);
    }
}

