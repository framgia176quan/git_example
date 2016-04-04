package com.example.framgia.weathersimple.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.framgia.weathersimple.R;
import com.example.framgia.weathersimple.data.CityObject;
import com.example.framgia.weathersimple.data.CloudsObject;
import com.example.framgia.weathersimple.data.CoordObject;
import com.example.framgia.weathersimple.data.ListObject;
import com.example.framgia.weathersimple.data.MainObject;
import com.example.framgia.weathersimple.data.WeatherDataObject;
import com.example.framgia.weathersimple.data.WeatherObject;
import com.example.framgia.weathersimple.data.WindObject;
import com.example.framgia.weathersimple.network.ImageDownloader;
import com.example.framgia.weathersimple.network.ServiceHandler;
import com.example.framgia.weathersimple.network.GetWeatherDataObject;
import com.example.framgia.weathersimple.ui.adapter.PagerAdapter;
import com.example.framgia.weathersimple.util.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SuppressLint("ValidFragment")
public class CityFragment extends Fragment{

    private TextView tvNameCity, tvDateTime, tvTemp, tvInfoWeather, tvTempNow, tvHumidityNow, tvPressureNow;
    private ImageView imgIcoWeather;
    private ConnectivityManager connMgr;
    private NetworkInfo networkInfo;
    private WeatherDataObject weatherDataObject;
    private String nameCity;
    private ViewPager viewPager;
    private Calendar calendar;
    private String strDayOfWeek;
    private String strMonthOfYear;
    private String strHour;
    private String strMinute;
    private CallBackData callBack;

    private static String api = "http://api.openweathermap.org/data/2.5/forecast?appid=b7f3847208e4a3fe8913be845bdb88df&mode=json&q=";

    private static String urlImage = "http://openweathermap.org/img/w/";

    public static final String TAG_CITY = "city";
    public static final String TAG_CITY_ID = "id";
    public static final String TAG_CITY_NAME = "name";
    public static final String TAG_CITY_COORD = "coord";
    public static final String TAG_CITY_COORD_LON = "lon";
    public static final String TAG_CITY_COORD_LAT = "lat";

    public static final String TAG_LIST = "list";
    public static final String TAG_LIST_MAIN = "main";
    public static final String TAG_LIST_MAIN_TEMP = "temp";
    public static final String TAG_LIST_MAIN_TEMP_MIN = "temp_min";
    public static final String TAG_LIST_MAIN_TEMP_MAX = "temp_max";
    public static final String TAG_LIST_MAIN_PRESSURE = "pressure";
    public static final String TAG_LIST_MAIN_HUMIDITY = "humidity";


    public static final String TAG_LIST_WEATHER = "weather";

    public static final String TAG_LIST_WEATHER_ID = "id";
    public static final String TAG_LIST_WEATHER_MAIN = "main";
    public static final String TAG_LIST_WEATHER_DESCRRIPTION = "description";
    public static final String TAG_LIST_WEATHER_ICON = "icon";

    public static final String TAG_LIST_CLOUDS = "clouds";
    public static final String TAG_LIST_CLOUDS_ALL = "all";

    public static final String TAG_LIST_WIND = "wind";
    public static final String TAG_LIST_WIND_SPEED = "speed";
    public static final String TAG_LIST_WIND_DEG = "deg";

    public static final String TAG_LIST_DT_TXT = "dt_txt";

    @SuppressLint("ValidFragment")
    public CityFragment(String nameCity) {
        this.nameCity = nameCity;

    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }



    public boolean isNetwork(){
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else  return false;

    }

    public interface CallBackData {
        void callBackCall(WeatherDataObject weatherDataObject);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
//        try {
//            CallBackData = (CallBackData) context;
//        } catch (ClassCastException e) {
////            throw new ClassCastException(context.toString()
////                    + " must implement OnHeadlineSelectedListener");
//        }
    }

    class ImageLoader extends ImageDownloader {

        public ImageLoader(Context mContext) {
            super(mContext);
        }

        @Override
        protected Bitmap doInBackground(String... param) {

            try {
                Bitmap bitmap = null;
                URL imageUrl = new URL(param[0]);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imgIcoWeather.setImageBitmap(result);
        }
    }
    class GetData extends GetWeatherDataObject{


        public GetData(Context mContext) {
            super(mContext);
        }


        @Override
        protected JSONObject doInBackground(String... arg0) {

            ServiceHandler serviceHandler = new ServiceHandler();

            // Making a request to url and getting response
            String url = null;
            try {
                url = api + URLEncoder.encode(nameCity, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ;
            String jsonStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET);
            JSONObject weatherJSONObject = new JSONObject();
//            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {

                try {
                    weatherJSONObject = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return weatherJSONObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                weatherDataObject = new WeatherDataObject();
                CityObject cityObject = new CityObject();
                ArrayList<ListObject> arrayListList = new ArrayList<ListObject>();

                JSONObject city = result.getJSONObject(TAG_CITY);
                String id = city.getString(TAG_CITY_ID);
                String name = city.getString(TAG_CITY_NAME);

                JSONObject coord = city.getJSONObject(TAG_CITY_COORD);
                String lon = coord.getString(TAG_CITY_COORD_LON);
                String lat = coord.getString(TAG_CITY_COORD_LAT);

                cityObject.setId(Integer.parseInt(id));
                cityObject.setName(name);
                cityObject.setCoord(new CoordObject(Double.parseDouble(lon), Double.parseDouble(lat)));

                JSONArray list = result.getJSONArray(TAG_LIST);

                for (int i = 0; i < list.length(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    JSONObject main = obj.getJSONObject(TAG_LIST_MAIN);
                    String temp = main.getString(TAG_LIST_MAIN_TEMP);
                    String temp_min = main.getString(TAG_LIST_MAIN_TEMP_MIN);
                    String temp_max = main.getString(TAG_LIST_MAIN_TEMP_MAX);
                    String pressure = main.getString(TAG_LIST_MAIN_PRESSURE);
                    String humidity = main.getString(TAG_LIST_MAIN_HUMIDITY);

                    MainObject mainObject = new MainObject();
                    mainObject.setTemp(Double.parseDouble(temp));
                    mainObject.setTempMin(Double.parseDouble(temp_min));
                    mainObject.setTempMax(Double.parseDouble(temp_max));
                    mainObject.setPressure(Double.parseDouble(pressure));
                    mainObject.setHumidity(Integer.parseInt(humidity));

                        Log.d("Quan", "" + mainObject.toString());


                    JSONArray weatherArray = obj.getJSONArray(TAG_LIST_WEATHER);
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String idWeather = weather.getString(TAG_LIST_WEATHER_ID);
                    String mainWeather = weather.getString(TAG_LIST_WEATHER_MAIN);
                    String description = weather.getString(TAG_LIST_WEATHER_DESCRRIPTION);
                    String icon = weather.getString(TAG_LIST_WEATHER_ICON);

                    ArrayList<WeatherObject> weatherObjects = new ArrayList<WeatherObject>();
                    weatherObjects.add(0, new WeatherObject(Integer.parseInt(idWeather), mainWeather, description, icon));

//                        Log.d("Quan", "" + weatherObjects.toString());

                    JSONObject clouds = obj.getJSONObject(TAG_LIST_CLOUDS);
                    String all = clouds.getString(TAG_LIST_CLOUDS_ALL);

                    CloudsObject cloudsObject = new CloudsObject();
                    cloudsObject.setAll(Integer.parseInt(all));

//                        Log.d("Quan", "" + cloudsObject.toString());

                    JSONObject wind = obj.getJSONObject(TAG_LIST_WIND);
                    String speed = wind.getString(TAG_LIST_WIND_SPEED);
                    String deg = wind.getString(TAG_LIST_WIND_DEG);

                    WindObject windObject = new WindObject();
                    windObject.setSpeed(Double.parseDouble(speed));
                    windObject.setDeg(Double.parseDouble(deg));

//                        Log.d("Quan", "" + windObject.toString());

                    String dt_txt = obj.getString(TAG_LIST_DT_TXT);
//                        Log.d("Quan", "" + dt_txt.toString());

                    arrayListList.add(new ListObject(mainObject, weatherObjects, cloudsObject, windObject, dt_txt));

                }

                weatherDataObject.setCity(cityObject);
                weatherDataObject.setList(arrayListList);


            } catch (Exception e) {
                e.printStackTrace();
            }
            bindDataToView(weatherDataObject);
            initPaging(weatherDataObject);
//            if(iSendData != null ){
                callBack.callBackCall(weatherDataObject);
//            }

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container , false);
        Log.d("VA tag", "view = " +view );

        initView(view);
        loadDataFromServer();

        return view;
    }

    public void initView(View view){
        LinearLayout llContent = (LinearLayout) view.findViewById(R.id.layout_content_detail_now);
        Log.d("VA tag", "llContent = " +llContent );
        tvNameCity = (TextView) view.findViewById(R.id.tv_name_city);
        Log.d("VA tag", "tvNameCity = " +tvNameCity );
        tvDateTime = (TextView) view.findViewById(R.id.tv_date_time);
        tvTemp = (TextView) view.findViewById(R.id.tv_temp);
        tvInfoWeather = (TextView) view.findViewById(R.id.tv_infor_weather);
        imgIcoWeather = (ImageView) view.findViewById(R.id.img_ico_weather);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager_sub);

        tvTempNow = (TextView) view.findViewById(R.id.tv_temp_now);
        tvHumidityNow = (TextView) view.findViewById(R.id.tv_humidity_now);
        tvPressureNow = (TextView) view.findViewById(R.id.tv_pressure_now);


    }


    public void initPaging(WeatherDataObject weatherDataObject){
        List<Fragment> fragments = new ArrayList<Fragment>();

        WeatherInWeekFragment weatherInWeekFragment  = new WeatherInWeekFragment();
//        Bundle bundle=new Bundle();
//        bundle.putSerializable("Quan", weatherDataObject);
//        weatherInWeekFragment.setArguments(bundle);

        WeatherInWeekFragment weatherInWeekFragment2  = new WeatherInWeekFragment();
        fragments.add(weatherInWeekFragment);
        fragments.add(weatherInWeekFragment2);

        PagerAdapter pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), fragments);

        viewPager.setAdapter(pagerAdapter);
    }
    public void bindDataToView(WeatherDataObject weatherDataObject){
        tvNameCity.setText("" + weatherDataObject.getCity().getName());
        getDateTime();
        tvDateTime.setText(strHour+":"+strMinute+" / "+strDayOfWeek+", "+calendar.get(Calendar.DAY_OF_MONTH)+", "+strMonthOfYear);
        tvTemp.setText("" + Tool.convertKenvilToCelcius(Float.parseFloat("" + weatherDataObject.getList().get(0).getMain().getTemp()))+ "\u2103");
        tvInfoWeather.setText("" + weatherDataObject.getList().get(0).getWeather().get(0).getMain());

        tvTempNow.setText("" + Tool.convertKenvilToCelcius(Float.parseFloat("" + weatherDataObject.getList().get(0).getMain().getTempMin())) + "\u2103" + " / " + Tool.convertKenvilToCelcius(Float.parseFloat("" + weatherDataObject.getList().get(0).getMain().getTempMax()))+ "\u2103");
        tvHumidityNow.setText("" + weatherDataObject.getList().get(0).getMain().getHumidity() + "%");
        tvPressureNow.setText("" + weatherDataObject.getList().get(0).getMain().getPressure() + "hPa");

        ImageLoader imageLoader = new ImageLoader(getActivity());
        imageLoader.execute("" + urlImage + weatherDataObject.getList().get(0).getWeather().get(0).getIcon() + ".png");
    }

    public void getDateTime(){

        Date date = new Date();

        strHour = (String) android.text.format.DateFormat.format("hh", date);
        strMinute = (String) android.text.format.DateFormat.format("mm", date);
        strDayOfWeek = (String) android.text.format.DateFormat.format("EEEE", date);
        strMonthOfYear = (String) android.text.format.DateFormat.format("MMM", date);


    }

    public void loadDataFromServer(){
        connMgr = (ConnectivityManager) getActivity()
                .getSystemService(getActivity().CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if(isNetwork()){
            GetData getData = new GetData(getActivity());
            getData.execute(nameCity);

        }
    }

}
