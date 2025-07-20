import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from '../screens/HomeScreen';
import ConsultationScreen from '../screens/ConsultationScreen';

const Stack = createNativeStackNavigator();

const AppNavigator = () => (
  <Stack.Navigator initialRouteName="Home">
    <Stack.Screen name="Home" component={HomeScreen} options={{ headerShown: false }} />
    <Stack.Screen name="Consultation" component={ConsultationScreen} options={{ title: 'Consultation' }} />
  </Stack.Navigator>
);

export default AppNavigator; 