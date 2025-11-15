// src/services/backgroundMotionService.js
import * as Location from 'expo-location';
import { LOCATION_TASK_NAME } from '../background/locationTask';

export async function startBackgroundLocation() {
  const { status } = await Location.requestForegroundPermissionsAsync();
  if (status !== 'granted') {
    throw new Error('Foreground location permission not granted');
  }

  const bgStatus = await Location.requestBackgroundPermissionsAsync();
  if (bgStatus.status !== 'granted') {
    throw new Error('Background location permission not granted');
  }

  const isRunning = await Location.hasStartedLocationUpdatesAsync(LOCATION_TASK_NAME);
  if (!isRunning) {
    await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
      accuracy: Location.Accuracy.High,
      timeInterval: 2000,      // ms
      distanceInterval: 0,     // meters; 0 = send every update
      showsBackgroundLocationIndicator: true, // iOS
      pausesUpdatesAutomatically: false,
      foregroundService: {
        notificationTitle: 'Detecting driving activity',
        notificationBody: 'Running in the background to detect motion.',
      },
    });
  }
}

export async function stopBackgroundLocation() {
  const isRunning = await Location.hasStartedLocationUpdatesAsync(LOCATION_TASK_NAME);
  if (isRunning) {
    await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
  }
}
