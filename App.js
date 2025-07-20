import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import AppNavigator from './src/navigation/AppNavigator';
import messaging from '@react-native-firebase/messaging';
import { Linking, NativeEventEmitter, NativeModules, Platform, DeviceEventEmitter, PermissionsAndroid, AppState } from 'react-native';
import { useNavigationContainerRef } from '@react-navigation/native';

const App = () => {
  const navigationRef = useNavigationContainerRef();

  useEffect(() => {
    async function setupCloudMessaging() {
      // Request notification permission for Android 13+
      if (Platform.OS === 'android' && Platform.Version >= 33) {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
          {
            title: 'Notification Permission',
            message: 'This app needs notification permission to receive incoming calls.',
            buttonPositive: 'OK',
          }
        );
        if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
          console.log('POST_NOTIFICATIONS permission denied');
          return;
        }
      }
      const authStatus = await messaging().requestPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;

      if (enabled) {
        const fcmToken = await messaging().getToken();
        console.log('FCM Token:', fcmToken);
      }
    }
    setupCloudMessaging();

    // Extract intent extras for deep linking
    const extractIntentExtras = async () => {
      if (Platform.OS === 'android') {
        const initialIntent = await NativeModules?.IntentLauncher?.getInitialIntent?.();
        console.log('extractIntentExtras: initialIntent', initialIntent);
        if (initialIntent && initialIntent.extras) {
          const { deepLink, callerName } = initialIntent.extras;
          console.log('extractIntentExtras: deepLink', deepLink, 'callerName', callerName);
          if (deepLink === 'consultation') {
            navigationRef.current?.navigate('Consultation', { astrologerName: callerName || 'Astrologer' });
          }
        }
      }
    };

    // Handle initial intent (cold start)
    extractIntentExtras();

    // Listen for new intents while app is running (warm start)
    const handleAppStateChange = async (nextAppState) => {
      if (nextAppState === 'active') {
        extractIntentExtras();
      }
    };
    AppState.addEventListener('change', handleAppStateChange);

    // Listen for new intents from native (onNewIntent)
    DeviceEventEmitter.addListener('onNewIntent', () => {
      extractIntentExtras();
    });

    // Listen for missed call broadcast from native
    if (Platform.OS === 'android') {
      const onMissedCall = async () => {
        console.log('App.js: Missed call broadcast received');
        // HomeScreen handles increment and storage
      };
      
      const onEndCall = () => {
        console.log('End call action received, navigating to Home');
        
        // Navigate to Home screen and reset navigation stack
        navigationRef.current?.reset({
          index: 0,
          routes: [{ name: 'Home' }],
        });
      };
      
      const missedCallSubscription = DeviceEventEmitter.addListener('com.vedazdemo.MISSED_CALL', onMissedCall);
      const endCallSubscription = DeviceEventEmitter.addListener('END_CALL_ACTION', onEndCall);
      
      return () => {
        missedCallSubscription.remove();
        endCallSubscription.remove();
        AppState.removeEventListener('change', handleAppStateChange);
      };
    }

    return () => {
      AppState.removeEventListener('change', handleAppStateChange);
    };
  }, []);

  return (
    <NavigationContainer ref={navigationRef}>
      <AppNavigator />
    </NavigationContainer>
  );
};

export default App;
