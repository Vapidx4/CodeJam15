import { Text, View } from "react-native";
import { useAppStateCount } from "./hooks/useAppStateCount";

export default function App() {
    const activeCount = useAppStateCount();

    return (
        <View className="flex-1 items-center justify-center bg-white">
            <Text className="text-xl font-bold text-blue-500">
                Welcome to Nativewind!
            </Text>
            <Text className="mt-4 text-lg">
                AppState was active {activeCount} times
            </Text>
        </View>
    );
}