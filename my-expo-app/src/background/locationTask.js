// src/background/locationTask.js
import * as TaskManager from 'expo-task-manager';

export const LOCATION_TASK_NAME = 'BACKGROUND_LOCATION_TASK';

const MOVING_SPEED_THRESHOLD_KMH = 10; // >10 → likely driving
const STOPPED_SPEED_THRESHOLD_KMH = 3; // <3 → likely stopped
const MOVING_MIN_DURATION_MS = 5000;   // 5 seconds above 10 km/h to declare moving
const STOPPED_MIN_DURATION_MS = 7000;  // 7 seconds below 3 km/h to declare stopped

let prevLoc = null;
let prevTimeMs = null;

let lastAboveMovingThresholdAt = null;
let lastBelowStoppedThresholdAt = null;
let isVehicleMoving = false;

function distanceMeters(loc1, loc2) {
  const R = 6371000;
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
  return R * c;
}

function computeSpeedKmh(prevLoc, loc, prevTimeMs, nowMs) {
  const dist = distanceMeters(prevLoc, loc); // m
  const dtSec = (nowMs - prevTimeMs) / 1000;
  if (dtSec <= 0) return 0;
  const speedMps = dist / dtSec;
  return speedMps * 3.6;
}

TaskManager.defineTask(LOCATION_TASK_NAME, ({ data, error }) => {
  if (error) {
    console.log('Background task error:', error);
    return;
  }
  const { locations } = data;
  if (!locations || locations.length === 0) return;

  const loc = locations[0];
  const now = Date.now();

  let speedKmh = null;

  // Prefer native speed if provided
  if (loc.coords.speed != null) {
    speedKmh = loc.coords.speed * 3.6;
  } else if (prevLoc && prevTimeMs) {
    speedKmh = computeSpeedKmh(prevLoc, loc, prevTimeMs, now);
  }

  prevLoc = loc;
  prevTimeMs = now;

  if (speedKmh == null) {
    return;
  }

  // --- classify moving vs stopped ---
  if (speedKmh > MOVING_SPEED_THRESHOLD_KMH) {
    if (!lastAboveMovingThresholdAt) {
      lastAboveMovingThresholdAt = now;
    }
    lastBelowStoppedThresholdAt = null;

    if (!isVehicleMoving && now - lastAboveMovingThresholdAt >= MOVING_MIN_DURATION_MS) {
      isVehicleMoving = true;
      console.log('Vehicle is now MOVING at', speedKmh.toFixed(1), 'km/h');
      // TODO: write to storage / trigger notification / update risk engine input
    }
  } else if (speedKmh < STOPPED_SPEED_THRESHOLD_KMH) {
    if (!lastBelowStoppedThresholdAt) {
      lastBelowStoppedThresholdAt = now;
    }
    lastAboveMovingThresholdAt = null;

    if (isVehicleMoving && now - lastBelowStoppedThresholdAt >= STOPPED_MIN_DURATION_MS) {
      isVehicleMoving = false;
      console.log('Vehicle is now STOPPED');
      // TODO: write to storage / reset risk engine state if you want
    }
  } else {
    // middle region (e.g. slowing down / creeping in traffic) → clear timers but don't flip state immediately
    lastAboveMovingThresholdAt = null;
    lastBelowStoppedThresholdAt = null;
  }
});
