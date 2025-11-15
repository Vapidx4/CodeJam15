// src/screens/SpeedLimitDebugScreen.js
import React, { useEffect, useState } from 'react';
import { View, Text, Button, StyleSheet } from 'react-native';
import { startSpeedWatch } from '../services/locationService';

export default function SpeedLimitDebugScreen() {
  const [location, setLocation] = useState(null);
  const [speedKmh, setSpeedKmh] = useState(null);
  const [error, setError] = useState(null);
  const [watching, setWatching] = useState(false);
  const [sub, setSub] = useState(null);

  const start = async () => {
    try {
      setError(null);
      const subscription = await startSpeedWatch(({ location, speedKmh }) => {
        setLocation(location);
        setSpeedKmh(speedKmh);
      });
      setSub(subscription);
      setWatching(true);
    } catch (e) {
      console.error('WATCH ERROR:', e);
      setError(e.message);
    }
  };

  const stop = () => {
    if (sub) {
      sub.remove();
    }
    setSub(null);
    setWatching(false);
  };

  useEffect(() => {
    // optional: auto-start watcher when screen mounts
    start();

    return () => {
      if (sub) {
        sub.remove();
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const lat = location?.coords?.latitude;
  const lng = location?.coords?.longitude;

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Speed Debug</Text>

      <View style={{ flexDirection: 'row', gap: 12, marginBottom: 16 }}>
        <Button
          title={watching ? 'Restart' : 'Start'}
          onPress={() => {
            if (watching) {
              stop();
              start();
            } else {
              start();
            }
          }}
        />
        {watching && <Button title="Stop" onPress={stop} />}
      </View>

      {error && <Text style={styles.error}>Error: {error}</Text>}

      {location && (
        <>
          <Text style={styles.text}>
            Lat: {lat?.toFixed(5)} | Lng: {lng?.toFixed(5)}
          </Text>
          <Text style={styles.text}>
            Speed: {speedKmh != null ? speedKmh.toFixed(1) + ' km/h' : 'calculating...'}
          </Text>
        </>
      )}

      {!location && !error && <Text style={styles.text}>Waiting for GPS...</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#111',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
    gap: 12,
  },
  title: {
    fontSize: 24,
    color: 'white',
    marginBottom: 16,
    fontWeight: 'bold',
  },
  text: {
    color: 'white',
  },
  error: {
    color: 'red',
  },
});
