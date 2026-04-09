module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: './android',
        packageImportPath: 'import com.shani.spinwheel.SpinWheelPackage;',
        packageInstance: 'new SpinWheelPackage()',
      },
    },
  },
};