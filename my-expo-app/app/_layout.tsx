import { Stack } from 'expo-router';
import React, { useEffect } from 'react';
import { startBackgroundLocation } from '../src/services/backgroundMotionService';

import '../global.css';


export default function RootLayout() {
  useEffect(() => {
    // fire once when the app mounts
    (async () => {
      try {
        await startBackgroundLocation();
        console.log('Background location started');
      } catch (e) {
        console.log('Failed to start background location:', e);
      }
    })();
  }, []);

  return (
    <Stack>
      <Stack.Screen name="index" options={{ 
        title: 'Welcome Home',
      }} />

      <Stack.Screen name="[...missing]" options={{ headerShown: false }} />
    </Stack>
  );
}