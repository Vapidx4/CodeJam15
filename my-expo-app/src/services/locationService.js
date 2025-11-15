// src/services/locationService.js
import * as Location from 'expo-location';

// One-shot location (still useful elsewhere)
export async function getCurrentLocation() {
  const { status } = await Location.requestForegroundPermissionsAsync();
  if (status !== 'granted') {
    throw new Error('Location permission not granted');
  }

  const loc = await Location.getCurrentPositionAsync({
    accuracy: Location.Accuracy.Balanced,
  });

  return loc;
}

export function getSpeedKmhFromLocation(loc) {
  const speedMps = loc?.coords?.speed;
  if (speedMps == null) return null;
  return speedMps * 3.6;
}

// Haversine distance in meters between two { coords: { latitude, longitude } }
function distanceMeters(loc1, loc2) {
  const R = 6371000; // earth radius in m
  const toRad = (deg) => (deg * Math.PI) / 180;

  const lat1 = loc1.coords.latitude;
  const lon1 = loc1.coords.longitude;
  const lat2 = loc2.coords.latitude;
  const lon2 = loc2.coords.longitude;

  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c; // meters
}

// Live speed watcher: calls onUpdate({ location, speedKmh }) every time
export async function startSpeedWatch(onUpdate) {
  const { status } = await Location.requestForegroundPermissionsAsync();
  if (status !== 'granted') {
    throw new Error('Location permission not granted');
  }

  let prevLoc = null;
  let prevTime = null;

  const subscription = await Location.watchPositionAsync(
    {
      accuracy: Location.Accuracy.High,
      timeInterval: 1000,   // ms between updates
      distanceInterval: 0,  // report even if distance small
    },
    (loc) => {
      const now = Date.now();
      let speedMps = loc.coords.speed;

      // If provider gives no speed or 0, compute it ourselves
      if (speedMps == null || speedMps === 0) {
        if (prevLoc && prevTime) {
          const dt = (now - prevTime) / 1000; // seconds
          if (dt > 0) {
            const dist = distanceMeters(prevLoc, loc); // meters
            speedMps = dist / dt;
          }
        }
      }

      const speedKmh = speedMps != null ? speedMps * 3.6 : null;

      onUpdate({ location: loc, speedKmh });

      prevLoc = loc;
      prevTime = now;
    }
  );

  return subscription; // you can call subscription.remove() to stop
}
