import { Text, View } from "react-native";
import { useAppStateCount } from "./hooks/useAppStateCount";
import { useDrivingDetection } from "./hooks/useDrivingDetection";

export default function App() {
    const activeCount = useAppStateCount();
    const isDriving = useDrivingDetection();

    console.log('Driving status:', isDriving); 

    return (
<View
    style={{
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: isDriving ? "#ef4444" : "#fff", // Tailwind bg-red-500 hex and white
    }}
>
    <Text className="text-xl font-bold text-blue-500">
        Welcome to Nativewind!
    </Text>
    <Text className="mt-4 text-lg">
        AppState was active {activeCount} times
    </Text>
    <Text className="mt-2 text-lg">
        Driving detection is {isDriving ? "ON" : "OFF"}
    </Text>
</View>
    );
}