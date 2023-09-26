import 'package:dio/dio.dart';
import 'package:impilo/app_state.dart';

class AuthTokenInterceptor extends Interceptor {
  static const skipHeader = 'skipAuthToken';

  Dio api;

  AuthTokenInterceptor(this.api);

  @override
  onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    final accessToken = FFAppState().accessToken;
    final path = options.path;
    if (accessToken != '' && !path.contains('/api/authenticate')) {
      options.headers['Authorization'] = 'Bearer $accessToken';
    }

    return super.onRequest(options, handler);
  }

  @override
  onError(DioError err, ErrorInterceptorHandler handler) async {
    return super.onError(err, handler);
  }

}
