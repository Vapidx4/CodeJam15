import { useEffect, useState } from "react";
import { AppState } from "react-native";

export function useAppStateCount(options = {}) {
    const { 
        countActive = true, 
        countBackground = true,
        fixDoubleCount = true 
    } = options;
    
    const [rawCount, setRawCount] = useState(0);

    useEffect(() => {
        const subscription = AppState.addEventListener("change", (nextAppState) => {
            const shouldCount = 
                (countActive && nextAppState === "active") ||
                (countBackground && nextAppState === "background");
            
            if (shouldCount) {
                setRawCount((count) => count + 1);
            }
        });

        return () => {
            subscription.remove();
        };
    }, [countActive, countBackground]);

    return fixDoubleCount ? Math.floor((rawCount + 1) / 2) : rawCount;
}