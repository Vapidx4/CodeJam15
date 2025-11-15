import { useEffect, useState, useRef } from "react";
import { Text, View, AppState } from "react-native";

export default function App() {
    const [activeCount, setActiveCount] = useState(0);
    const actualCount = Math.floor((activeCount + 1) / 2);

    const isMounted = useRef(false);

    useEffect(() => {
        isMounted.current = true;
        
        const subscription = AppState.addEventListener("change", (nextAppState) => {
            if (nextAppState === "active" || nextAppState === "background") {
                setActiveCount((count) => count + 1);
            }
        });

        return () => {
            isMounted.current = false;
            subscription.remove();
        };
    }, []);

    return (
        <View className="flex-1 items-center justify-center bg-white">
            <Text className="text-xl font-bold text-blue-500">
                Welcome to Nativewind!
            </Text>
            <Text className="mt-4 text-lg">
                AppState was active {actualCount} times
            </Text>
        </View>
    );
}