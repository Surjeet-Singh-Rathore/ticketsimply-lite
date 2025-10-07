# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes Exceptions
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep class com.bitla.ts.** {*;}
-keep class com.bitla.ts.presentation.viewModel.** { *; }
-keep class com.bitla.ts.domain.** {*;}
-keep class com.bitla.ts.domain.pojo.** {*;}
-keep class com.bitla.ts.domain.repository.** {*;}
-keep class com.bitla.ts.koin.** {*;}
-keep class com.bitla.ts.data.** {*;}
-keep class com.bitla.ts.presentation.** {*;}
-keep class com.bitla.ts.utils.** {*;}
-keep class com.bitla.ts.app.base.** {*;}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep class com.google.firebase.quickstart.database.java.viewholder.** {*;}
-keepclassmembers class com.google.firebase.quickstart.database.java.models.** {*;}
-keepclassmembers class com.google.firebase.quickstart.database.kotlin.models.** {*;}

-keepnames class com.firebase.** { *; }
-keepnames class com.shaded.fasterxml.jackson.** { *; }
-keepnames class org.shaded.apache.** { *; }
-keepnames class javax.servlet.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.commons.logging.impl.**


# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit.** { *; }
-keep class com.google.appengine.** { *; }
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform

-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault


# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

##---------razorpay----------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}

-dontwarn android.os.ServiceManager
-dontwarn com.odm.tty.TtyDevice
-dontwarn com.pos.sdk.accessory.PosAccessoryManager$EventListener
-dontwarn com.pos.sdk.accessory.PosAccessoryManager
-dontwarn com.pos.sdk.card.PosCardInfo
-dontwarn com.pos.sdk.card.PosCardManager
-dontwarn com.pos.sdk.cardreader.PosCardReaderManager
-dontwarn com.pos.sdk.cardreader.PosIccCardReader
-dontwarn com.pos.sdk.cardreader.PosMagCardReader
-dontwarn com.pos.sdk.cardreader.PosMifareCardReader
-dontwarn com.pos.sdk.cardreader.PosPiccCardReader
-dontwarn com.pos.sdk.emvcore.PosEmvAppList
-dontwarn com.pos.sdk.emvcore.PosEmvCapk
-dontwarn com.pos.sdk.emvcore.PosEmvCoreManager$EventListener
-dontwarn com.pos.sdk.emvcore.PosEmvCoreManager
-dontwarn com.pos.sdk.emvcore.PosEmvParam
-dontwarn com.pos.sdk.emvcore.PosEmvSmCapk
-dontwarn com.pos.sdk.emvcore.PosTermInfo
-dontwarn com.pos.sdk.printer.PosPrintStateInfo
-dontwarn com.pos.sdk.printer.PosPrinter$EventListener
-dontwarn com.pos.sdk.printer.PosPrinter$Parameters
-dontwarn com.pos.sdk.printer.PosPrinter
-dontwarn com.pos.sdk.printer.PosPrinterInfo
-dontwarn com.pos.sdk.security.PedKcvInfo
-dontwarn com.pos.sdk.security.PedKeyInfo
-dontwarn com.pos.sdk.security.PedRsaPinKey
-dontwarn com.pos.sdk.security.PosSecurityManager$EventListener
-dontwarn com.pos.sdk.security.PosSecurityManager
-dontwarn com.pos.sdk.servicemanager.PosServiceManager
-dontwarn com.pos.sdk.utils.PosByteArray
-dontwarn com.pos.sdk.utils.PosUtils
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

 -keep class com.bitla.restaurant_app.presentation.pojo.** {*;}
 -keep class com.bitla.restaurant_app.presentation.utils.PreferenceUtils { *; }