
## Common ##

-useuniqueclassmembernames

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature
-keepclassmembers enum * { *; }

## Support Lib ##

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

## Fabric ##

-keep class com.crashlytics.android.**

## Stetho ##
-dontwarn com.facebook.stetho.**

## Test frameworks ##

-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

## RxJava ##
-dontwarn rx.**
-keep class rx.** { *; }

## Serialization ##

-keep class com.dgsd.shifttracker.model.** { *; }

## Butterknife ##

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## Immutables ##

-dontwarn org.immutables.**

## OpenCSV ##

-dontwarn au.com.bytecode.opencsv.**

## Timber ##
-dontwarn timber.log.**

## Google Play Services ##

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}