const {
  withAndroidManifest,
  withInfoPlist,
  withSettingsGradle,
  createRunOncePlugin,
} = require('@expo/config-plugins');

const pkg = require('./package.json');

const withBackgroundGeolocation = (config, props = {}) => {
  config = withAndroidManifest(config, (config) => {
    const permissions = [
      'android.permission.ACCESS_COARSE_LOCATION',
      'android.permission.ACCESS_FINE_LOCATION',
      'android.permission.ACCESS_BACKGROUND_LOCATION',
      'android.permission.FOREGROUND_SERVICE',
      'android.permission.WAKE_LOCK',
    ];

    const manifest = config.modResults;
    if (!manifest.manifest['uses-permission']) {
      manifest.manifest['uses-permission'] = [];
    }

    const existingPermissions = manifest.manifest['uses-permission'].map(
      (p) => p.$ && p.$['android:name']
    );

    permissions.forEach((permission) => {
      if (!existingPermissions.includes(permission)) {
        manifest.manifest['uses-permission'].push({
          $: {
            'android:name': permission,
          },
        });
      }
    });

    return config;
  });

  config = withSettingsGradle(config, (config) => {
    const commonModuleString = `
include ':@ednaldocordeiro_react-native-background-geolocation-common'
project(':@ednaldocordeiro_react-native-background-geolocation-common').projectDir = new File(rootProject.projectDir, '../node_modules/@ednaldocordeiro/react-native-background-geolocation/android/common')
`;
    if (!config.modResults.contents.includes(':@ednaldocordeiro_react-native-background-geolocation-common')) {
      config.modResults.contents += commonModuleString;
    }
    return config;
  });

  config = withInfoPlist(config, (config) => {
    if (!config.modResults.UIBackgroundModes) {
      config.modResults.UIBackgroundModes = [];
    }
    if (!config.modResults.UIBackgroundModes.includes('location')) {
      config.modResults.UIBackgroundModes.push('location');
    }

    if (props.locationAlwaysUsageDescription !== false) {
      config.modResults.NSLocationAlwaysUsageDescription =
        props.locationAlwaysUsageDescription || 'App requires background tracking';
    }
    if (props.locationAlwaysAndWhenInUseUsageDescription !== false) {
      config.modResults.NSLocationAlwaysAndWhenInUseUsageDescription =
        props.locationAlwaysAndWhenInUseUsageDescription || 'App requires background tracking';
    }
    if (props.motionUsageDescription !== false) {
      config.modResults.NSMotionUsageDescription =
        props.motionUsageDescription || 'App requires motion tracking';
    }
    if (props.locationWhenInUseUsageDescription !== false) {
      config.modResults.NSLocationWhenInUseUsageDescription =
        props.locationWhenInUseUsageDescription || 'App requires background tracking';
    }

    return config;
  });

  return config;
};

module.exports = createRunOncePlugin(
  withBackgroundGeolocation,
  pkg.name,
  pkg.version
);
