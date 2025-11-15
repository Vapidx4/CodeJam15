import * as Location from 'expo-location';
import { useEffect, useState } from 'react';

export function useDrivingDetection() {
  const [isDriving, setIsDriving] = useState(false);

  useEffect(() => {
    let subscriber;
    let lastMovingTime = Date.now();

    (async () => {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') return;

      subscriber = await Location.watchPositionAsync(
        { accuracy: Location.Accuracy.Highest, distanceInterval: 1, timeInterval: 1000 },
        (location) => {
          const speedKmh = (location.coords.speed || 0) * 3.6;

          if (speedKmh > 0.0010) { // driving threshold
            setIsDriving(true);
            lastMovingTime = Date.now();
          } else {
            // if stationary for 2 seconds
            if (Date.now() - lastMovingTime > 2000) {
              setIsDriving(false);
            }
          }
        }
      );
    })();

    return () => subscriber?.remove();
  }, []);

  return isDriving;
}
