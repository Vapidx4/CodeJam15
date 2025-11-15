import { ScreenContent } from 'components/ScreenContent';
import { StatusBar } from 'expo-status-bar';
import { useRouter } from 'expo-router';
import { Button } from 'react-native';

export default function App() {
  const router = useRouter();

  return (
    <>
      <ScreenContent title="Home" path="App.tsx"></ScreenContent>
      <StatusBar style="auto" />
      <Button title="Go to Test 1" onPress={() => router.navigate('/test1')} />
      <Button title="Go to Test 2" onPress={() => router.navigate('/test2')} />
      <Button title="Go to Test 3" onPress={() => router.navigate('/test3')} />
      <Button title="go to speedlimit test" onPress={() => router.navigate('/speedlimit-debug')} />
    </>
  );
}
