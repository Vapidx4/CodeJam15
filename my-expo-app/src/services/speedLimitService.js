// src/services/speedLimitService.js
import { getCurrentLocation } from './locationService';

const ROADS_API_KEY = '';

// 1) Low-level: get speed limit from lat/lng
export async function getSpeedLimitForCoords(lat, lng) {
  const path = `${lat},${lng}`;
  const url =
    `https://roads.googleapis.com/v1/speedLimits?path=${encodeURIComponent(
      path
    )}&key=${ROADS_API_KEY}`;

  try {
    const response = await fetch(url);

    if (!response.ok) {
      // Log it for debugging, but DON'T crash the app
      console.log('Roads API error status:', response.status);
      const text = await response.text();
      console.log('Roads API body:', text);

      // For 403 / unavailable → just say "no speed limit"
      return null;
    }

    const data = await response.json();

    if (!data.speedLimits || data.speedLimits.length === 0) {
      return null;
    }

    const limitObj = data.speedLimits[0];

    const value = limitObj.speedLimit;
    const unit = limitObj.units || limitObj.speedLimitUnit; // KPH / MPH

    let kmh = value;
    if (unit === 'MPH') {
      kmh = value * 1.60934;
    }

    return kmh;
  } catch (err) {
    console.log('Roads API fetch failed:', err);
    return null;
  }
}

// You can keep this if you like, but it's now just a wrapper:
export async function getSpeedLimitForCurrentRoad() {
  const loc = await getCurrentLocation();
  const { latitude, longitude } = loc.coords;

  const speedLimitKmh = await getSpeedLimitForCoords(latitude, longitude);

  return {
    latitude,
    longitude,
    speedLimitKmh,
  };
}
