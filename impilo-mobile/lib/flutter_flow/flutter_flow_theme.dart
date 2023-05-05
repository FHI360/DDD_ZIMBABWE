// ignore_for_file: overridden_fields, annotate_overrides

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'package:shared_preferences/shared_preferences.dart';

const kThemeModeKey = '__theme_mode__';
SharedPreferences? _prefs;

enum DeviceSize {
  mobile,
  tablet,
  desktop,
}

abstract class FlutterFlowTheme {
  static DeviceSize deviceSize = DeviceSize.mobile;

  static Future initialize() async =>
      _prefs = await SharedPreferences.getInstance();
  static ThemeMode get themeMode {
    final darkMode = _prefs?.getBool(kThemeModeKey);
    return darkMode == null
        ? ThemeMode.system
        : darkMode
            ? ThemeMode.dark
            : ThemeMode.light;
  }

  static void saveThemeMode(ThemeMode mode) => mode == ThemeMode.system
      ? _prefs?.remove(kThemeModeKey)
      : _prefs?.setBool(kThemeModeKey, mode == ThemeMode.dark);

  static FlutterFlowTheme of(BuildContext context) {
    deviceSize = getDeviceSize(context);
    return Theme.of(context).brightness == Brightness.dark
        ? LightModeTheme()
        : LightModeTheme();
  }

  late Color primaryColor;
  late Color secondaryColor;
  late Color tertiaryColor;
  late Color alternate;
  late Color primaryBackground;
  late Color secondaryBackground;
  late Color primaryText;
  late Color secondaryText;

  late Color primaryBtnText;
  late Color lineColor;
  late Color customColor1;
  late Color backgroundComponents;
  late Color overlay;
  late Color noColor;

  String get title1Family => typography.title1Family;
  TextStyle get title1 => typography.title1;
  String get title2Family => typography.title2Family;
  TextStyle get title2 => typography.title2;
  String get title3Family => typography.title3Family;
  TextStyle get title3 => typography.title3;
  String get subtitle1Family => typography.subtitle1Family;
  TextStyle get subtitle1 => typography.subtitle1;
  String get subtitle2Family => typography.subtitle2Family;
  TextStyle get subtitle2 => typography.subtitle2;
  String get bodyText1Family => typography.bodyText1Family;
  TextStyle get bodyText1 => typography.bodyText1;
  String get bodyText2Family => typography.bodyText2Family;
  TextStyle get bodyText2 => typography.bodyText2;

  Typography get typography => {
        DeviceSize.mobile: MobileTypography(this),
        DeviceSize.tablet: TabletTypography(this),
        DeviceSize.desktop: DesktopTypography(this),
      }[deviceSize]!;
}

DeviceSize getDeviceSize(BuildContext context) {
  final width = MediaQuery.of(context).size.width;
  if (width < 479) {
    return DeviceSize.mobile;
  } else if (width < 991) {
    return DeviceSize.tablet;
  } else {
    return DeviceSize.desktop;
  }
}

class LightModeTheme extends FlutterFlowTheme {
  late Color primaryColor = const Color(0xFF1B3D33);
  late Color secondaryColor = const Color(0xFFCF7B0A);
  late Color tertiaryColor = const Color(0xFF9D1414);
  late Color alternate = const Color(0xFFB8E6E0);
  late Color primaryBackground = const Color(0xFFF1F4F8);
  late Color secondaryBackground = const Color(0xFFFFFFFF);
  late Color primaryText = const Color(0xFF0F1113);
  late Color secondaryText = const Color(0xFF57636C);

  late Color primaryBtnText = Color(0xFFFFFFFF);
  late Color lineColor = Color(0xFFE0E3E7);
  late Color customColor1 = Color(0xFF2FB73C);
  late Color backgroundComponents = Color(0xFF1D2428);
  late Color overlay = Color(0xB3FFFFFF);
  late Color noColor = Color(0x00FFFFFF);
}

abstract class Typography {
  String get title1Family;
  TextStyle get title1;
  String get title2Family;
  TextStyle get title2;
  String get title3Family;
  TextStyle get title3;
  String get subtitle1Family;
  TextStyle get subtitle1;
  String get subtitle2Family;
  TextStyle get subtitle2;
  String get bodyText1Family;
  TextStyle get bodyText1;
  String get bodyText2Family;
  TextStyle get bodyText2;
}

class MobileTypography extends Typography {
  MobileTypography(this.theme);

  final FlutterFlowTheme theme;

  String get title1Family => 'Roboto';
  TextStyle get title1 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 32.0,
      );
  String get title2Family => 'Roboto';
  TextStyle get title2 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get title3Family => 'Roboto';
  TextStyle get title3 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get subtitle1Family => 'Roboto';
  TextStyle get subtitle1 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get subtitle2Family => 'Roboto';
  TextStyle get subtitle2 => GoogleFonts.getFont(
        'Roboto',
        color: theme.primaryColor,
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get bodyText1Family => 'Roboto';
  TextStyle get bodyText1 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF262D34),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
  String get bodyText2Family => 'Roboto';
  TextStyle get bodyText2 => GoogleFonts.getFont(
        'Roboto',
        color: Color(0xFF95A1AC),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
}

class TabletTypography extends Typography {
  TabletTypography(this.theme);

  final FlutterFlowTheme theme;

  String get title1Family => 'Lexend';
  TextStyle get title1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 32.0,
      );
  String get title2Family => 'Lexend';
  TextStyle get title2 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get title3Family => 'Lexend';
  TextStyle get title3 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get subtitle1Family => 'Lexend';
  TextStyle get subtitle1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get subtitle2Family => 'Lexend';
  TextStyle get subtitle2 => GoogleFonts.getFont(
        'Lexend',
        color: theme.primaryColor,
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get bodyText1Family => 'Lexend';
  TextStyle get bodyText1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF262D34),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
  String get bodyText2Family => 'Lexend';
  TextStyle get bodyText2 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF95A1AC),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
}

class DesktopTypography extends Typography {
  DesktopTypography(this.theme);

  final FlutterFlowTheme theme;

  String get title1Family => 'Lexend';
  TextStyle get title1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 32.0,
      );
  String get title2Family => 'Lexend';
  TextStyle get title2 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get title3Family => 'Lexend';
  TextStyle get title3 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 24.0,
      );
  String get subtitle1Family => 'Lexend';
  TextStyle get subtitle1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF14181B),
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get subtitle2Family => 'Lexend';
  TextStyle get subtitle2 => GoogleFonts.getFont(
        'Lexend',
        color: theme.primaryColor,
        fontWeight: FontWeight.w500,
        fontSize: 18.0,
      );
  String get bodyText1Family => 'Lexend';
  TextStyle get bodyText1 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF262D34),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
  String get bodyText2Family => 'Lexend';
  TextStyle get bodyText2 => GoogleFonts.getFont(
        'Lexend',
        color: Color(0xFF95A1AC),
        fontWeight: FontWeight.normal,
        fontSize: 16.0,
      );
}

class DarkModeTheme extends FlutterFlowTheme {
  late Color primaryColor = const Color(0xFF689689);
  late Color secondaryColor = const Color(0xFF50756B);
  late Color tertiaryColor = const Color(0xFF14181B);
  late Color alternate = const Color(0xFFB8E6E0);
  late Color primaryBackground = const Color(0xFF1A1F24);
  late Color secondaryBackground = const Color(0xFF0F1113);
  late Color primaryText = const Color(0xFFFFFFFF);
  late Color secondaryText = const Color(0xFF95A1AC);

  late Color primaryBtnText = Color(0xFFFFFFFF);
  late Color lineColor = Color(0xFF22282F);
  late Color customColor1 = Color(0xFF452FB7);
  late Color backgroundComponents = Color(0xFF1D2428);
  late Color overlay = Color(0xB314181B);
  late Color noColor = Color(0x000F1113);
}

extension TextStyleHelper on TextStyle {
  TextStyle override({
    String? fontFamily,
    Color? color,
    double? fontSize,
    FontWeight? fontWeight,
    double? letterSpacing,
    FontStyle? fontStyle,
    bool useGoogleFonts = true,
    TextDecoration? decoration,
    double? lineHeight,
  }) =>
      useGoogleFonts
          ? GoogleFonts.getFont(
              fontFamily!,
              color: color ?? this.color,
              fontSize: fontSize ?? this.fontSize,
              letterSpacing: letterSpacing ?? this.letterSpacing,
              fontWeight: fontWeight ?? this.fontWeight,
              fontStyle: fontStyle ?? this.fontStyle,
              decoration: decoration,
              height: lineHeight,
            )
          : copyWith(
              fontFamily: fontFamily,
              color: color,
              fontSize: fontSize,
              letterSpacing: letterSpacing,
              fontWeight: fontWeight,
              fontStyle: fontStyle,
              decoration: decoration,
              height: lineHeight,
            );
}
