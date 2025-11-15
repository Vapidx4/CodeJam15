import React, { useEffect, useState } from "react";
import { View, Text, Button } from "react-native";
import * as Location from "expo-location";

export default function TestLocationScreen() {
  const [status, setStatus] = useState<string>("idle");
  const [coords, setCoords] = useState<any>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const getLocation = async () => {
    try {
      setStatus("requesting permission...");
      setErrorMsg(null);

      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        setStatus("permission denied");
        setErrorMsg("Location permission not granted");
        return;
      }

      setStatus("getting location...");
      const pos = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.High,
      });

      setCoords(pos.coords);
      setStatus("success");
    } catch (e: any) {
      console.log("Location error:", e);
      setStatus("error");
      setErrorMsg(e?.message ?? "Unknown error");
    }
  };

  useEffect(() => {
    // or call getLocation() automatically here
  }, []);

  return (
    <View style={{ flex: 1, padding: 24, justifyContent: "center" }}>
      <Text>Status: {status}</Text>
      {errorMsg && <Text>Error: {errorMsg}</Text>}
      {coords && (
        <Text>
          lat: {coords.latitude}, lon: {coords.longitude}
        </Text>
      )}
      <Button title="Get location" onPress={getLocation} />
    </View>
  );
}
