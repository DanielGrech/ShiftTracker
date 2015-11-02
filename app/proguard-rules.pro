
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