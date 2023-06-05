import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:impilo/app_state.dart';
import 'package:impilo/main.dart';
import 'package:oktoast/oktoast.dart';

import 'AuthTokenInterceptor.dart';

class ErrorDialogInterceptor extends Interceptor {
  static const skipHeader = 'skipDialog';

  @override
  onError(DioError err, ErrorInterceptorHandler handler) async {
    final data = err.response?.data;

    final refreshToken = FFAppState().refreshToken;

    if (data == null ||
        !(data is Map) ||
        err.response?.statusCode == 401 &&
            (refreshToken != '') &&
            !err.requestOptions.headers
                .containsKey(AuthTokenInterceptor.skipHeader)) {
      return super.onError(err, handler);
    }
    if (err.response?.statusCode == 403 &&
        data['path'] == '/api/authenticate') {
      showToast(
        'Invalid username or password',
        duration: Duration(seconds: 2),
        position: ToastPosition.bottom,
        backgroundColor: Colors.red,
        radius: 3.0,
        textStyle: TextStyle(fontSize: 15.0),
      );
      return super.onError(err, handler);
    }

    if (err.response?.statusCode == 403) {
      showToast(
        'Session expired, signing out',
        duration: Duration(seconds: 5),
        position: ToastPosition.bottom,
        backgroundColor: Colors.red,
        radius: 3.0,
        textStyle: TextStyle(fontSize: 15.0),
      );
      FFAppState().refreshToken = '';
      FFAppState().accessToken = '';
      FFAppState().code = '';

      router.pushNamed("loginPage");
    }
    showToast(
      'An error occurred accessing the backend',
      duration: Duration(seconds: 2),
      position: ToastPosition.bottom,
      backgroundColor: Colors.redAccent,
      radius: 1.0,
      textStyle: TextStyle(fontSize: 15.0),
    );
    return super.onError(err, handler);
  }
}
