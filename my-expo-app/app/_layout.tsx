import { Stack } from 'expo-router';
import '../global.css';


export default function RootLayout() {
  return (
    <Stack>
      <Stack.Screen name="index" options={{ 
        title: 'Welcome Home',
      }} />

      <Stack.Screen name="[...missing]" options={{ headerShown: false }} />
    </Stack>
  );
}