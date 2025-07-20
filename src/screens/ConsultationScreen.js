import React from 'react';
import { View, Text, Button, StyleSheet, Platform, NativeModules, Alert } from 'react-native';

const ConsultationScreen = ({ route, navigation }) => {
  const { astrologerName = 'Astrologer' } = route.params || {};

  const endCall = () => {
    console.log('End consultation button pressed');
    
    // Try to cancel the call in progress notification if native module exists
    if (Platform.OS === 'android' && NativeModules.EndCallModule) {
      try {
        console.log('Calling EndCallModule.endCall()');
        NativeModules.EndCallModule.endCall();
        console.log('EndCallModule.endCall() called successfully');
      } catch (e) {
        console.log('Could not call native module:', e.message);
      }
    } else {
      console.log('EndCallModule not available');
    }
    
    // Always navigate back to home screen
    console.log('Navigating to Home screen');
    navigation.reset({
      index: 0,
      routes: [{ name: 'Home' }],
    });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Connected with {astrologerName}</Text>
      <Text style={styles.status}>Consultation in progress...</Text>
      <Button 
        title="End Consultation" 
        onPress={endCall} 
        color="#D4AF37" 
      />
      <Button 
        title="Test Navigation" 
        onPress={() => {
          console.log('Test navigation button pressed');
          navigation.navigate('Home');
        }} 
        color="#FF6B6B" 
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#2C1D4F',
  },
  title: {
    color: '#D4AF37',
    fontSize: 22,
    marginBottom: 20,
  },
  status: {
    color: '#FFFFFF',
    fontSize: 16,
    marginBottom: 40,
  },
});

export default ConsultationScreen; 