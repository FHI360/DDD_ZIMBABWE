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
        'Wrong username or password',
        duration: Duration(seconds: 2),
        position: ToastPosition.bottom,
        backgroundColor: Colors.red,
        radius: 3.0,
        textStyle: TextStyle(fontSize: 15.0),
      );
      return super.onError(err, handler);
    }

    if (err.response?.statusCode == 401) {
      var body = err.response?.data!;
      var message = 'Session expired; please sign in again';
      if (body['detail'] == 'ACCESS.MANAGEMENT.ERRORS.WRONG_CREDENTIALS') {
        message = 'Wrong username or password';
      } else if (body['detail'] ==
          'ACCESS.MANAGEMENT.ERRORS.FAILED_ATTEMPTS_LOCK') {
        message = 'Your account has been locked due to repeated failed logins; please contact administrator';
      } else if (body['detail'] ==
          'ACCESS.MANAGEMENT.ERRORS.ACCOUNT_DISABLED') {
        message = 'Your account has been locked; please contact administrator';
      } else if (body['detail'] == 'ACCESS.MANAGEMENT.ERRORS.TOKEN_EXPIRED') {
        message = 'Session expired; please sign in again';
      }
      showToast(
        message,
        duration: Duration(seconds: 15),
        position: ToastPosition.bottom,
        backgroundColor: Colors.red,
        radius: 4.0,
        textStyle: TextStyle(fontSize: 15.0),
      );
      FFAppState().refreshToken = '';
      FFAppState().accessToken = '';
      FFAppState().code = '';
      FFAppState().name = '';

      router.pushNamed('login');
      return super.onError(err, handler);
    }

    if (data['path'] != '/api/ddd/device-profile') {
      showToast(
        'A error occurred processing request; please try again later',
        duration: Duration(seconds: 2),
        position: ToastPosition.bottom,
        backgroundColor: Colors.redAccent,
        radius: 1.0,
        textStyle: TextStyle(fontSize: 15.0),
      );
    }
    return super.onError(err, handler);
  }
}
