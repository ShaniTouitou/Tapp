import React from 'react';
import {
  requireNativeComponent,
  StyleProp,
  ViewStyle,
} from 'react-native';

export type SpinWheelProps = {
  configUrl: string;
  style?: StyleProp<ViewStyle>;
};

const NativeSpinWheel =
  requireNativeComponent<SpinWheelProps>('SpinWheelView');

export default function SpinWheelNativeComponent(props: SpinWheelProps) {
  return <NativeSpinWheel {...props} />;
}