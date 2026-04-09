const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const path = require('path');

const spinwheelRoot = path.resolve(__dirname, '../react-native-spinwheel');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('@react-native/metro-config').MetroConfig}
 */
const config = {
  watchFolders: [spinwheelRoot],
  resolver: {
    nodeModulesPaths: [path.resolve(__dirname, 'node_modules')],
    blockList: [
      new RegExp(`${spinwheelRoot.replace(/\\/g, '\\\\')}[\\\\/]android[\\\\/]build[\\\\/].*`),
    ],
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
