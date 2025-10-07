package com.bitla.ts.utils;

import static com.bitla.ts.utils.common.UtilKt.getAppVersionCode;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bitla.ts.BuildConfig;
import com.bitla.ts.domain.pojo.dashboard_model.privilege.PrivilegeModel;
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel;
import com.bitla.ts.domain.pojo.remote_config.UpdateCountryListData;
import com.bitla.ts.utils.constants.AppConstantsKt;
import com.bitla.ts.utils.sharedPref.PreferenceUtils;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Taiyab Ali on 12-Feb-2022.
 */

public class RemoteConfigUpdateHelper {

    public static String KEY_UPDATE_ENABLE = AppConstantsKt.isUpdate;
    public static String KEY_MANUAL_UPDATE_POPUP = AppConstantsKt.manualUpdatePopup;
    public static String KEY_IN_APP_UPDATE_TS = AppConstantsKt.inAppUpdateTS;
    public static String KEY_UPDATE_VERSION = AppConstantsKt.versionCode;
    public static String KEY_UPDATE_URL = AppConstantsKt.updateAppUrl;
    public static String KEY_UPDATE_TITLE = AppConstantsKt.updateTitle;
    public static String KEY_UPDATE_DESCRIPTION = AppConstantsKt.updateDescription;
    public static String KEY_UPDATE_IS_CRITICAL = AppConstantsKt.isUpdateCritical;
    public static String KEY_UPDATE_PACKAGE_NAME = AppConstantsKt.tsPackageName;
    public static String KEY_DESTINATION_PAIR_TIME = AppConstantsKt.destinationPairTime;
    public static String KEY_DESTINATION_PAIR_CACHE_ENABLE = AppConstantsKt.isDestinationPairCacheEnable;
    public static String KEY_SEND_OPERATOR_REPORT = AppConstantsKt.sendErrorReport;
    public static String location_popup_string = AppConstantsKt.location_popup_string;

    public static String KEY_UPDATE_COUNTRY_LIST = AppConstantsKt.updateCountryList;
    public static String KEY_IS_SENTRY_ENABLED = AppConstantsKt.isSentryEnabled;
    public static String KEY_SENTRY_FOR_COUNTRY = AppConstantsKt.sentryForCountry;
    public static String KEY_SENTRY_PERFORMANCE_FOR_COUNTRY = AppConstantsKt.sentryPerformanceForCountry;
    public static String LOCATION_API_INTERVAL = AppConstantsKt.locationApiInterval;

    public interface onUpdateCheckClickListener {
        void onCheckRemoteConfigUpdateListener(
                String urlApp,
                String title,
                String description,
                String packageName,
                Boolean isCritical,
                Boolean isManualUpdatePop,
                Boolean inAppUpdateTS,
                String updateCountryList);
    }


    public interface OnLocationApiIntervalFetchListener {
        void onLocationIntervalFetchListener(String interval);
    }



    public interface onDestinationPairCacheCheckClickListener {
        void onCheckRemoteConfigUpdateListener(
                Integer destinationPairTime,
                Boolean isDestinationPairCacheEnable);
    }

    public interface OnSendErrorReportListener {
        void onErrorCheckListener(
                Boolean isSendErrorReportEnable);
    }

    public interface SentryListener {
        void onSentryListener(Boolean isSentryEnabled, String country, String currentCountry, String sentryPerformanceForCountry);
    }


    public interface LocationPopup {
        void locationPop(
                String loationPopString);
    }

    public static BuilderMy with(Context context) {
        return new BuilderMy(context);
    }

    private final SentryListener sentryListener;

    private final OnSendErrorReportListener onSendErrorReportListener;
    private final onUpdateCheckClickListener onCheckRemoteConfigUpdateListener;
    private final onDestinationPairCacheCheckClickListener onDestinationPairCacheCheckClickListener;
    private final OnLocationApiIntervalFetchListener onLocationApiIntervalFetchListener;
    private final LocationPopup locationPopup;
    private final Context context;

    public RemoteConfigUpdateHelper(Context context, RemoteConfigUpdateHelper.onUpdateCheckClickListener onUpdateCheckClickListener, RemoteConfigUpdateHelper.onDestinationPairCacheCheckClickListener onDestinationPairCacheCheckClickListener, RemoteConfigUpdateHelper.OnSendErrorReportListener onSendErrorReportListener, RemoteConfigUpdateHelper.LocationPopup locationPopup, RemoteConfigUpdateHelper.SentryListener sentryListener, RemoteConfigUpdateHelper.OnLocationApiIntervalFetchListener onLocationApiIntervalFetchListener) {
        this.onCheckRemoteConfigUpdateListener = onUpdateCheckClickListener;
        this.onDestinationPairCacheCheckClickListener = onDestinationPairCacheCheckClickListener;
        this.onSendErrorReportListener = onSendErrorReportListener;
        this.sentryListener = sentryListener;
        this.locationPopup = locationPopup;
        this.context = context;
        this.onLocationApiIntervalFetchListener = onLocationApiIntervalFetchListener;

    }


    public static class BuilderMy {

        private final Context context;
        private onUpdateCheckClickListener onUpdateCheckClickListener;
        private OnLocationApiIntervalFetchListener onLocationApiIntervalFetchListener;
        private onDestinationPairCacheCheckClickListener onDestinationPairCacheCheckClickListener;
        private OnSendErrorReportListener onSendErrorReportListener;
        private SentryListener sentryListener;
        private LocationPopup locationPopup;


        public BuilderMy(Context context) {
            this.context = context;
        }

        public BuilderMy onUpdateCheck(onUpdateCheckClickListener onUpdateCheckClickListener) {
            this.onUpdateCheckClickListener = onUpdateCheckClickListener;
            return this;
        }



        public BuilderMy onLocationIntervalFetch(OnLocationApiIntervalFetchListener onLocationApiIntervalFetchListener) {
            this.onLocationApiIntervalFetchListener = onLocationApiIntervalFetchListener;
            return this;
        }

        public BuilderMy onDestinationPairCheck(onDestinationPairCacheCheckClickListener onDestinationPairCacheCheckClickListener) {
            this.onDestinationPairCacheCheckClickListener = onDestinationPairCacheCheckClickListener;
            return this;
        }

        public BuilderMy onCheckErrorReportEnabled(OnSendErrorReportListener onSendErrorReportListener) {
            this.onSendErrorReportListener = onSendErrorReportListener;
            return this;
        }

        public BuilderMy onCheckIsSentryEnabled(SentryListener sentryListener) {
            this.sentryListener = sentryListener;
            return this;
        }


        public RemoteConfigUpdateHelper build() {
            return new RemoteConfigUpdateHelper(context, onUpdateCheckClickListener, onDestinationPairCacheCheckClickListener, onSendErrorReportListener, locationPopup, sentryListener, onLocationApiIntervalFetchListener);
        }

        public BuilderMy LocationPopUpListner(LocationPopup locationPopup) {
            this.locationPopup = locationPopup;
            return this;
        }


        public RemoteConfigUpdateHelper check() {
            RemoteConfigUpdateHelper remoteConfigUpdate_helper = build();
            remoteConfigUpdate_helper.check();
            return remoteConfigUpdate_helper;
        }

    }

    public void check() {
        PrivilegeAsyncTask asyncTask = new PrivilegeAsyncTask();
        asyncTask.execute();
    }

    private class PrivilegeAsyncTask extends AsyncTask<Void, Void, PrivilegeResponseModel> {
        @Override
        protected PrivilegeResponseModel doInBackground(Void... voids) {
            try {
                return PreferenceUtils.INSTANCE.getPrivilegejJava();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(PrivilegeResponseModel result) {
            String currentCountry = "";
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            try {
                String obj = firebaseRemoteConfig.getString(KEY_UPDATE_COUNTRY_LIST);
                Gson g = new Gson();
                UpdateCountryListData updateDetailsList = g.fromJson(obj, UpdateCountryListData.class);
                int currentAppVersionCode = 0;

                if (result != null) {
                    if (result.isChileApp()) {
                        currentCountry = "Chile";
                    } else {
                        currentCountry = result.getCountry();
                    }
                }

                if(updateDetailsList.is_global_update() != null){
                    if(updateDetailsList.is_global_update()){
                        currentAppVersionCode = updateDetailsList.getGlobal_update_details().getVersion();
                    }else {
                        if(!updateDetailsList.getUse_privilege_version_code()){
                            if(result.isChileApp()){
                                currentAppVersionCode = updateDetailsList.getCountry().get(2).getVersion();
                            }else {
                                for (int i = 0; i < updateDetailsList.getCountry().size(); i++) {
                                    if(currentCountry.toLowerCase(Locale.ROOT).equals(updateDetailsList.getCountry().get(i).getCountry_name())){
                                        currentAppVersionCode = updateDetailsList.getCountry().get(i).getVersion();
                                    }
                                }
                            }

                        }else {
                            if(result.getAppSubmissionHistory() != null){
                                currentAppVersionCode = Integer.parseInt(result.getAppSubmissionHistory().getAndroid().getVersionNumber());

                            }
                        }

                    }
                }
//            Timber.d("onCheckRemoteConfig"+currentAppVersionCode);

                // int currentAppVersionCode = Integer.parseInt(firebaseRemoteConfig.getString(KEY_UPDATE_VERSION));
                String title = firebaseRemoteConfig.getString(KEY_UPDATE_TITLE);
                String description = firebaseRemoteConfig.getString(KEY_UPDATE_DESCRIPTION);
                String packageName = firebaseRemoteConfig.getString(KEY_UPDATE_PACKAGE_NAME);
                Boolean isCritical = firebaseRemoteConfig.getBoolean(KEY_UPDATE_IS_CRITICAL);
                Boolean isManualUpdatePop = firebaseRemoteConfig.getBoolean(KEY_MANUAL_UPDATE_POPUP);
                Boolean inAppUpdateTS = firebaseRemoteConfig.getBoolean(KEY_IN_APP_UPDATE_TS);
                int appVersionCode = Integer.parseInt(getAppVersionCode(context));
                String updateURL = firebaseRemoteConfig.getString(KEY_UPDATE_URL);

                if (appVersionCode < currentAppVersionCode && onCheckRemoteConfigUpdateListener != null) {
//                Timber.d("onCheckRemoteConfig"+onCheckRemoteConfigUpdateListener);
                    onCheckRemoteConfigUpdateListener.onCheckRemoteConfigUpdateListener(
                            updateURL,
                            title,
                            description,
                            packageName,
                            isCritical,
                            isManualUpdatePop,
                            inAppUpdateTS,
                            obj
                    );
                }
            }catch (Exception e){
                if(BuildConfig.DEBUG){
                    e.printStackTrace();
                }
            }

            if (!firebaseRemoteConfig.getString(KEY_DESTINATION_PAIR_TIME).isEmpty()) {
                int destinationPairTime = Integer.parseInt(firebaseRemoteConfig.getString(KEY_DESTINATION_PAIR_TIME));
                Boolean isDestinationPairCacheEnable = firebaseRemoteConfig.getBoolean(KEY_DESTINATION_PAIR_CACHE_ENABLE);

                if (onDestinationPairCacheCheckClickListener != null) {
                    onDestinationPairCacheCheckClickListener.onCheckRemoteConfigUpdateListener(destinationPairTime, isDestinationPairCacheEnable);
                }
            }


            if (!firebaseRemoteConfig.getString(LOCATION_API_INTERVAL).isEmpty()) {

                if (onLocationApiIntervalFetchListener != null) {
                    onLocationApiIntervalFetchListener.onLocationIntervalFetchListener(firebaseRemoteConfig.getString(LOCATION_API_INTERVAL));
                }

            }




            Boolean isSendErrorReport = firebaseRemoteConfig.getBoolean(KEY_SEND_OPERATOR_REPORT);
            if (onSendErrorReportListener != null)
                onSendErrorReportListener.onErrorCheckListener(isSendErrorReport);

            Boolean isSentryEnabled = firebaseRemoteConfig.getBoolean(KEY_IS_SENTRY_ENABLED);
            String sentryForCountry = firebaseRemoteConfig.getString(KEY_SENTRY_FOR_COUNTRY);
            String sentryPerformanceForCountry = firebaseRemoteConfig.getString(KEY_SENTRY_PERFORMANCE_FOR_COUNTRY);
            if (sentryListener != null)
                sentryListener.onSentryListener(isSentryEnabled,sentryForCountry,currentCountry,sentryPerformanceForCountry);


            String locationPopUpString = firebaseRemoteConfig.getString(location_popup_string);

            if (locationPopup != null) {
                locationPopup.locationPop(locationPopUpString);
            }
        }
    }

}


