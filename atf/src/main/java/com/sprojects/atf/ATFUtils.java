package com.sprojects.atf;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class ATFUtils
{

    // #####################################################################

    public static class ATFApplication extends Application
    {
        private Database database = null;

        // #####################################################################

        @Override
        public void onCreate()
        {
            super.onCreate();

            startSession();
        }

        // #####################################################################

        @Override
        public void onTerminate()
        {
            closeDatabase();
            super.onTerminate();
        }

        // #####################################################################

        private void startSession()
        {
            openDatabase("main_db");
        }

        // #####################################################################

        private void openDatabase(String dataBaseName)
        {
            DatabaseConfiguration config = new DatabaseConfiguration(getApplicationContext());
            try{
                database = new Database(dataBaseName, config);

            }catch (CouchbaseLiteException e) {

            }
        }

        // #####################################################################

        private void closeDatabase()
        {
            if(database != null)
            {
                try{
                    database.close();

                } catch (CouchbaseLiteException e) {
                }
            }
        }

    }


    // #####################################################################

    public static class XDataManager
    {
        private Context context = null;
        private Database database = null;
        private ATFApplication application;

        // #####################################################################

        public XDataManager(Context context)
        {
            this.context = context;

            try {

                this.application = (ATFApplication) ((Activity)context).getApplication();

            }catch (Exception e){}

            if(this.application != null) this.database = this.application.database;
        }

        // #####################################################################

        public MutableDocument getOrCreateDocument(String docId)
        {
            if(database == null) return null;
            if(docId == null || docId.equals("")) return null;

            try {
                Document doc = database.getDocument(docId);

                if(doc != null)
                {
                    return doc.toMutable();

                }else{

                    MutableDocument mutableDoc = new MutableDocument(docId);
                    return mutableDoc.toMutable();
                }

            }catch (Exception e) {}

            return null;
        }

        // #####################################################################

        public Document getDocument(String docId)
        {
            if(database == null) return null;
            if(docId == null || docId.equals("")) return null;

            try {

                Document doc = database.getDocument(docId);
                if(doc != null) return doc;

            }catch (Exception e) {}

            return null;
        }

        // #####################################################################

        public boolean saveData(String docId, String key, String data)
        {
            if(database == null) return false;

            MutableDocument doc = getOrCreateDocument(docId);
            doc.setString(key, data);

            try{
                database.save(doc);
                return true;

            } catch (CouchbaseLiteException e) {
                Log.e("CouchbaseLiteException", "Failed to save the doc");
            }

            return false;
        }

        // #####################################################################

        public String retrieveData(String docId, String key)
        {
            if(database == null) return null;

            MutableDocument doc = getOrCreateDocument(docId);

            if(doc != null) return doc.getString(key);

            return null;
        }

        // #####################################################################

        public ArrayList<String> retrieveAll(String docId)
        {
            ArrayList<String> data = new ArrayList<>();

            if(database == null) return data;

            MutableDocument doc = getOrCreateDocument(docId);
            if(doc == null) return data;

            List<String> keys = doc.getKeys();

            return (ArrayList<String>) keys;
        }

        // #####################################################################

        public boolean removeDocument(String docId)
        {
            if(database == null) return false;

            try {
                Document doc = getDocument(docId);
                if(doc != null)
                {
                    database.delete(doc);
                    return true;
                }

            } catch (CouchbaseLiteException e) {
                Log.e("CouchbaseLiteException", "Failed to delete the doc");
            }

            return false;
        }
    }


    // #####################################################################

    public static class SimpleLVAdapter extends BaseAdapter
    {
        LayoutInflater layoutInflater;
        Context context;
        ArrayList<View> Views;

        public SimpleLVAdapter(Context context, ArrayList<View> views)
        {
            super();
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.Views = views;
        }

        @Override
        public int getCount()
        {
            //
            if(Views == null) return 1;
            if(Views.isEmpty()) return 1;
            if(Views.size() == 0) return 1;

            return Views.size();
        }

        @Override
        public Object getItem(int position)
        {
            //
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            //
            return position;
        }

        @Override
        public boolean isEnabled(int position)
        {
            try {

                View v = Views.get(position);
                return (v.isEnabled()) ? true : false;

            }catch (Exception e){}

            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = null;

            try {

                v = (Views.get(position));

            }catch (Exception e){}

            if(v == null)
            {
                v = ATFViews.viewVerticalSpace(context,10, false);
                //v = this.layoutInflater.inflate(R.layout.lvc_blank,null);
            }

            return v;
        }

    }


    // #####################################################################

    public static class ActionSheet
    {
        protected Context context;
        public BottomSheetDialog dialog;
        public ArrayList<View> viewItems;

        // #####################################################################

        public ActionSheet(Context context)
        {
            this.context = context;
            this.dialog = new BottomSheetDialog(context);
            this.viewItems = new ArrayList<>();
        }

        // #####################################################################

        public void addTitle(String title)
        {
            if(title == null || title.equals("")) return;
            if(viewItems == null) return;

            viewItems.add(ATFViews.viewActionSheetTitle(context, title));
        }

        // #####################################################################

        public void cancel()
        {
            dialog.cancel();
        }

        // #####################################################################

        public void show(ArrayList<View> views, boolean addCancelView)
        {
            if(views == null || views.size() == 0) return;

            viewItems.add(ATFViews.viewVerticalSpace(context, 10, false));
            viewItems.addAll(views);

            if(addCancelView)
            {
                View cancelView = ATFViews.viewTitleIconAction(context, context.getString(R.string.cancel), R.drawable.back, new Runnable() {
                    @Override
                    public void run() {
                        cancel();
                    }
                });

                viewItems.add(cancelView);
            }

            viewItems.add(ATFViews.viewVerticalSpace(context, 10, false));

            View lv = simpleListView(dialog.getContext(), viewItems);

            dialog.setContentView(lv);
            dialog.show();

        }
    }


    // #####################################################################

    public static class XLocation
    {

        // #####################################################################

        public static interface XLocationInterface
        {
            void onSuccess(XLocation.GPSCoordinates location);
            void onError(String message);
        }

        // #####################################################################

        public static interface XLocationStatusInterface
        {
            void onValidStatus();
            void onError(String message);
        }


        // #####################################################################


        public static boolean checkLocationEnabled(Context context)
        {
            int locationMode = 0;
            String locationProviders;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            }else{
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }


        }

        // #####################################################################

        public static boolean checkLocationPermission(Context context)
        {
            //
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        // #####################################################################

        public static void requestLocationPermission(Context context)
        {
            //
            int ACCESS_FINE_LOCATION_CODE = 1001;
            ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }

        // #####################################################################

        public static void checkValidLocationStatus(final Context context, final XLocationStatusInterface xLocationStatusInterface)
        {
            if(!checkLocationEnabled(context))
            {
                xLocationStatusInterface.onError(getStringResourceByName(context, "location_not_enabled"));
                return;
            }

            if(!checkLocationPermission(context))
            {
                Dexter.withActivity(((Activity) context))
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response){
                                //
                                xLocationStatusInterface.onValidStatus();

                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response){
                                //
                                xLocationStatusInterface.onError(getStringResourceByName(context, "location_no_permission"));
                            }

                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                //
                                token.continuePermissionRequest();
                            }
                        })

                        .withErrorListener(new PermissionRequestErrorListener() {

                            //
                            @Override public void onError(DexterError error) {
                                xLocationStatusInterface.onError(getStringResourceByName(context, "error"));
                            }

                        }).check();
            }

            xLocationStatusInterface.onValidStatus();
        }

        // #####################################################################

        public static void findLocation(final Context context, final XLocationInterface callback)
        {
            try {

                final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isNetworkEnabled)
                {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        //
                        @Override
                        public void onLocationChanged(Location location){
                            callback.onSuccess(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                        }

                        @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                        @Override public void onProviderEnabled(String provider) { }
                        @Override public void onProviderDisabled(String provider) { }
                    }, null);

                }else{

                    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (isGPSEnabled)
                    {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                        locationManager.requestSingleUpdate(criteria, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                callback.onSuccess(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                            }

                            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                            @Override public void onProviderEnabled(String provider) { }
                            @Override public void onProviderDisabled(String provider) { }
                        }, null);
                    }
                }

            }catch (SecurityException exp) {
                //
            }

        }


        // #####################################################################

        public static class GPSCoordinates
        {
            public float longitude = -1;
            public float latitude = -1;

            public GPSCoordinates(float theLatitude, float theLongitude)
            {
                longitude = theLongitude;
                latitude = theLatitude;
            }

            public GPSCoordinates(double theLatitude, double theLongitude)
            {
                longitude = (float) theLongitude;
                latitude = (float) theLatitude;
            }
        }
    }


    // #####################################################################

    public interface RunnableWithObject
    {
        public void run(Object object);
    }

    // #####################################################################

    private static SecurePreferences getAppDataInstance(Context context)
    {
        if(context != null) return  new SecurePreferences(context, "app_prefs", "du9Ms9bNdv3A3b1dVce8x3Q0d4c3rlF8", true);
        return null;
    }


    // #####################################################################

    public static void saveAppData(Context context, String k, String v)
    {
        SecurePreferences preferences = getAppDataInstance(context);
        if(preferences != null) preferences.put(k, v);
    }


    // #####################################################################

    public static String getAppData(Context context, String k)
    {
        SecurePreferences preferences = getAppDataInstance(context);
        if(preferences != null) return preferences.getString(k);

        return null;
    }


    // #####################################################################
    // Before Java 8
    public static String implodeListString(String separator, String... data)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length - 1; i++)
        {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *"))
            {
                //empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }

        sb.append(data[data.length - 1].trim());

        return sb.toString();
    }


    // #####################################################################

    public static String generateRandomString()
    {
        try {

            int charCount = 8;

            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            StringBuilder sb = new StringBuilder(charCount);
            Random random = new Random();
            for (int i = 0; i < charCount; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            String output = sb.toString();

            return output;

        }catch (Exception e)
        {}

        return null;
    }


    // #####################################################################

    public static void vibration(Context context)
    {
        try {

            Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            if(v != null) v.vibrate(500);

        }catch (Exception e)
        {}
    }


    // #####################################################################

    public static String floatToMoneyString(float money)
    {
        try {

            NumberFormat formatter = new DecimalFormat("#0.00");
            return formatter.format(money);

        }catch (Exception e)
        {}

        return null;
    }


    // #####################################################################

    public static void toast(Context context, String text)
    {
        try{

            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

        }catch (Exception ex){}
    }


    // #####################################################################

    public static void makeFocus(Context context, EditText editText)
    {
        try{
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        }catch (Exception ex){}
    }


    // #####################################################################

    public static int getScreenWidth(Context context)
    {
        try{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = (wm != null) ? wm.getDefaultDisplay(): null;
            Point size = new Point();
            if(display != null)
            {
                display.getSize(size);
                return size.x;
            }

        }catch (Exception ex){}

        return 0;
    }


    // #####################################################################

    public static int getScreenHeight(Context context)
    {
        try{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = (wm != null) ? wm.getDefaultDisplay(): null;
            Point size = new Point();
            if(display != null)
            {
                display.getSize(size);
                return size.y;
            }

        }catch (Exception ex){}

        return 0;
    }


    // #####################################################################

    public static boolean checkInternetConnection(Context context)
    {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm == null) return false;

            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if(netInfo != null && netInfo.isConnectedOrConnecting()) return true;

        }catch (Exception ex){}

        return false;
    }


    // #####################################################################

    public static void hideKeyboard(Activity activity)
    {
        try{

            View view = activity.findViewById(android.R.id.content);
            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }catch (Exception ex){
            //
        }
    }


    // #####################################################################

    public static String reverseDate(String date)
    {
        try{
            String split[] = date.split("/");

            if(split.length < 3) return date;

            String rd = "";
            rd += split[2];
            rd += "/";
            rd += split[1];
            rd += "/";
            rd += split[0];

            return rd;

        }catch (Exception ex){}

        return null;
    }


    // #####################################################################

    public static int getColor(Context context, int id)
    {
        try{
            final int version = Build.VERSION.SDK_INT;

            if (version >= 23)
            {
                return ContextCompat.getColor(context, id);
            } else {
                return context.getResources().getColor(id);
            }

        }catch (Exception ex){}

        return 0;
    }


    // #####################################################################

    public static ListView simpleListView(Context context, ArrayList<View> views)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.lv);
            if(v == null) return null;

            ListView LV = (ListView)v.findViewById(R.id.listview);

            SimpleLVAdapter LVAdapter = new SimpleLVAdapter(context, views);
            LV.setAdapter(LVAdapter);

            LV.setDivider(new ColorDrawable(context.getResources().getColor(R.color.colorAccent)));

            SwipeRefreshLayout SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.list_view_swipe_to_refresh);

            if(SwipeRefresh != null) SwipeRefresh.setEnabled(false);

            ViewGroup vg = (ViewGroup) LV.getParent();
            vg.removeView(LV);

            LV.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LV.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            return LV;

        }catch (Exception ex){}

        return null;
    }


    // #####################################################################

    public static void showImageFromURL(Context context, ImageView imageView, String Url)
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.displayImage(Url, imageView);

        try {
            //
        }catch (Exception e){}
    }


    // #####################################################################

    public static int getResId(String resName, Class<?> c)
    {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // #####################################################################

    public static int getResourceId(Context context, String resourceName, String resourceType)
    {
        try {
            String pPackageName = context.getPackageName();
            return context.getResources().getIdentifier(resourceName, resourceType, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // #####################################################################

    public static String getStringResourceByName(Context context, String aString)
    {
        try {

            String packageName = context.getPackageName();
            int resId = context.getResources().getIdentifier(aString, "string", packageName);
            return context.getString(resId);

        }catch (Exception e) {

        }

        return null;
    }


    // #####################################################################

    public static void simpleAlert(Context context, String title, String message)
    {
        try {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle(title);
            alertDialog.setMessage(message);

            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            };

            alertDialog.setPositiveButton(R.string.ok_done, cancel);
            alertDialog.show();

        }catch (Exception e){}
    }


    // #####################################################################

    public static void alert(Context context, String title, String message, String positiveButtonTitle, final Runnable runnable)
    {
        try {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle(title);
            alertDialog.setMessage(message);

            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            };

            if(runnable != null)
            {
                DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runnable.run();
                    }
                };

                alertDialog.setPositiveButton(positiveButtonTitle, ocl);
            }

            alertDialog.setNegativeButton(R.string.cancel, cancel);
            alertDialog.show();

        }catch (Exception e){}

    }


    // #####################################################################

    public static void inputAlert(Context context, String title, String text, final RunnableWithObject runnableWithObject)
    {
        inputAlert(context, title, text, InputType.TYPE_CLASS_TEXT, runnableWithObject);
    }


    // #####################################################################

    public static void inputAlert(Context context, String title, String text, int inputType, final RunnableWithObject runnableWithObject)
    {
        try {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            //
            View v = ATFViews.viewEditText(context,1000, title, text, inputType, true);
            //View v = getViewByResId(context, R.layout.child_input_alert_edit_text);

            if(v == null) return;

            final EditText tvInput = v.findViewById(R.id.view_edit_text_et);

            //
            makeFocus(context, tvInput);

            if(text != null && !text.equals(""))
            {
                tvInput.setText(text);
                tvInput.selectAll();
            }

            //
            alertDialog.setView(v);

            //
            alertDialog.setPositiveButton(getStringResourceByName(context, "ok"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runnableWithObject.run(tvInput.getText().toString());
                }
            });

            //
            alertDialog.setNegativeButton(getStringResourceByName(context, "cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();

        }catch (Exception e){}

    }


    // #####################################################################

    public static void showToastError(Context context, String message)
    {
        try{
            Toasty.error(context, message, Toast.LENGTH_LONG, true).show();

        }catch(Exception e)
        {}
    }


    // #####################################################################

    public static void showToastInfo(Context context, String message)
    {
        try{
            Toasty.normal(context, message, Toast.LENGTH_LONG, context.getResources().getDrawable(getResourceId(context,"info", "drawable"))).show();

        }catch(Exception e)
        {}
    }


    // #####################################################################

    public static void actionDelayed(long delayMillis, Runnable runnable)
    {
        final Handler handler = new Handler();
        handler.postDelayed(runnable, delayMillis);
    }
}
