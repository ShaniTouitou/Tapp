import React from 'react';
import { SafeAreaView, StyleSheet, Text, View } from 'react-native';
import SpinWheel from 'react-native-spinwheel';

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>DEMO SPIN WHEEL</Text>

      <View style={styles.wrapper}>
        <SpinWheel
          configUrl="https://gist.githubusercontent.com/ShaniTouitou/fa5024f0e34c804a7e41635e100e8f85/raw/gistfile1.txt"
          style={{ width: 300, height: 300 }}
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 26,
    fontFamily: 'Poppins-Bold',
    marginBottom: 24,
    color: '#7B4FBF',
    letterSpacing: 2,
  },
  wrapper: {
    justifyContent: 'center',
    alignItems: 'center',
  },
});