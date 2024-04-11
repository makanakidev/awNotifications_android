import 'package:awesome_notifications/src/utils/media_abstract_utils.dart'
    if (dart.library.html) 'package:awesome_notifications/src/utils/media_abstract_utils_web.dart';
import 'package:flutter/material.dart';

class AwesomeAudioUtils extends AwesomeMediaUtils {
  /// FACTORY METHODS *********************************************

  factory AwesomeAudioUtils() => _instance;

  @visibleForTesting
  AwesomeAudioUtils.private();

  static final AwesomeAudioUtils _instance = AwesomeAudioUtils.private();

  /// FACTORY METHODS *********************************************

  @override
  getFromMediaAsset(String mediaPath) {
    return null;

  }

  @override
  getFromMediaFile(String mediaPath) {
    return null;

  }

  @override
  getFromMediaNetwork(String mediaPath) {
    return null;

  }

  @override
  getFromMediaResource(String mediaPath) {
    return null;

    /*
        String cleanPath = BitmapUtils.cleanMediaPath(mediaPath);
        rootBundle.loadString(cleanPath).then((value){
          print(value);
        });
        break;
        */
  }
}
