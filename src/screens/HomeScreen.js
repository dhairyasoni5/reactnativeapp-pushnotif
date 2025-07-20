import React, { useState, useCallback, useEffect } from 'react';
import { View, Text, Button, StyleSheet, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect } from '@react-navigation/native';
import { DeviceEventEmitter, NativeEventEmitter, NativeModules } from 'react-native';

const HomeScreen = ({ navigation }) => {
  const [missedCalls, setMissedCalls] = useState(0);

  // Load missed call count from AsyncStorage
  const loadMissedCallCount = async () => {
    try {
      const count = await AsyncStorage.getItem('missedCalls');
      const parsedCount = count ? parseInt(count, 10) : 0;
      setMissedCalls(parsedCount);
      console.log('Loaded missed call count:', parsedCount);
    } catch (error) {
      console.error('Failed to load missed call count:', error);
      setMissedCalls(0);
    }
  };

  // Increment missed call count
  const incrementMissedCallCount = async () => {
    try {
      const newCount = missedCalls + 1;
      await AsyncStorage.setItem('missedCalls', newCount.toString());
      setMissedCalls(newCount);
      console.log('Incremented missed call count to:', newCount);
    } catch (error) {
      console.error('Failed to increment missed call count:', error);
    }
  };

  // Clear missed call count
  const clearMissedCalls = async () => {
    try {
      await AsyncStorage.setItem('missedCalls', '0');
      setMissedCalls(0);
      console.log('Cleared missed call count');
      Alert.alert('Success', 'Missed calls cleared!');
    } catch (error) {
      console.error('Failed to clear missed call count:', error);
      Alert.alert('Error', 'Failed to clear missed calls');
    }
  };

  // Load count when screen comes into focus
  useFocusEffect(
    useCallback(() => {
      loadMissedCallCount();
    }, [])
  );

  // Listen for missed call events
  useEffect(() => {
    const onMissedCall = async () => {
      console.log('Missed call event received');
      await incrementMissedCallCount();
    };

    // Use NativeEventEmitter for more reliable event listening
    const eventEmitter = new NativeEventEmitter(NativeModules.MissedCallModule);
    const subscription = eventEmitter.addListener('MISSED_CALL_EVENT', onMissedCall);

    // Also listen for backward compatibility
    const deviceEventSubscription = DeviceEventEmitter.addListener('com.vedazdemo.MISSED_CALL', onMissedCall);

    return () => {
      subscription.remove();
      deviceEventSubscription.remove();
    };
  }, [missedCalls]);

  // Test function to simulate missed call
  const testMissedCall = async () => {
    console.log('Simulating missed call');
    await incrementMissedCallCount();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Vedaz Demo</Text>
      <Text style={styles.status}>Status: Waiting for call...</Text>
      
      {missedCalls > 0 && (
        <View style={styles.badgeContainer}>
          <Text style={styles.badge}>Missed Calls: {missedCalls}</Text>
          <Button title="Clear" onPress={clearMissedCalls} color="#FF6B6B" />
        </View>
      )}
      
      <View style={styles.buttonContainer}>
        <Button
          title="Go to Consultation (Test)"
          onPress={() => navigation.navigate('Consultation')}
          color="#D4AF37"
        />
        <Button
          title="Test Missed Call"
          onPress={testMissedCall}
          color="#4CAF50"
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { 
    flex: 1, 
    alignItems: 'center', 
    justifyContent: 'center',
    backgroundColor: '#2C1D4F'
  },
  title: { 
    fontSize: 24, 
    fontWeight: 'bold',
    color: '#D4AF37',
    marginBottom: 20
  },
  status: { 
    marginVertical: 20,
    color: '#FFFFFF'
  },
  badgeContainer: { 
    flexDirection: 'row', 
    alignItems: 'center', 
    marginBottom: 20,
    gap: 10
  },
  badge: { 
    color: '#D4AF37', 
    fontWeight: 'bold',
    fontSize: 16
  },
  buttonContainer: {
    gap: 10
  }
});

export default HomeScreen;